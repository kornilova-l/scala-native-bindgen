#include "TreeVisitor.h"
#include "clang/AST/RecordLayout.h"

bool TreeVisitor::VisitFunctionDecl(clang::FunctionDecl *func) {
    if (!astContext->getSourceManager().isInMainFile(func->getLocation())) {
        /* include functions only from the original header */
        return true;
    }
    std::string funcName = func->getNameInfo().getName().getAsString();
    std::shared_ptr<Type> retType =
        typeTranslator.translate(func->getReturnType());
    std::vector<std::shared_ptr<Parameter>> parameters;

    int anonCounter = 0;

    for (const clang::ParmVarDecl *parm : func->parameters()) {
        // Handle default values
        std::string pname = parm->getNameAsString();

        if (pname.empty()) {
            pname = "anonymous" + std::to_string(anonCounter++);
        }

        std::shared_ptr<Type> ptype = typeTranslator.translate(parm->getType());
        parameters.emplace_back(std::make_shared<Parameter>(pname, ptype));
    }

    ir.addFunction(funcName, std::move(parameters), retType,
                   func->isVariadic());

    return true;
}

bool TreeVisitor::VisitTypedefDecl(clang::TypedefDecl *tpdef) {
    std::string name = tpdef->getName();

    std::shared_ptr<Type> type =
        typeTranslator.translate(tpdef->getUnderlyingType());
    if (type) {
        ir.addTypeDef(name, type, getLocation(tpdef));
    }
    return true;
}

bool TreeVisitor::VisitEnumDecl(clang::EnumDecl *enumdecl) {
    std::string name = enumdecl->getNameAsString();

    if (name.empty() && enumdecl->getTypedefNameForAnonDecl()) {
        name = enumdecl->getTypedefNameForAnonDecl()->getNameAsString();
    }

    std::vector<Enumerator> enumerators;

    for (const clang::EnumConstantDecl *en : enumdecl->enumerators()) {
        int64_t value = en->getInitVal().getSExtValue();
        enumerators.emplace_back(en->getNameAsString(), value);
    }

    std::string scalaType = typeTranslator.getTypeFromTypeMap(
        enumdecl->getIntegerType().getUnqualifiedType().getAsString());

    ir.addEnum(name, scalaType, std::move(enumerators), getLocation(enumdecl));

    return true;
}

bool TreeVisitor::VisitRecordDecl(clang::RecordDecl *record) {
    std::string name = record->getNameAsString();

    // Handle typedef struct {} x; and typedef union {} y; by getting the name
    // from the typedef
    if ((record->isStruct() || record->isUnion()) && name.empty() &&
        record->getTypedefNameForAnonDecl()) {
        name = record->getTypedefNameForAnonDecl()->getNameAsString();
    }

    if (record->isUnion() && record->isThisDeclarationADefinition() &&
        !record->isAnonymousStructOrUnion() && !name.empty()) {
        handleUnion(record, name);
        return true;

    } else if (record->isStruct() && record->isThisDeclarationADefinition() &&
               !record->isAnonymousStructOrUnion() && !name.empty()) {
        handleStruct(record, name);
        return true;
    }
    return false;
}

void TreeVisitor::handleUnion(clang::RecordDecl *record, std::string name) {
    std::vector<std::shared_ptr<Field>> fields;

    for (const clang::FieldDecl *field : record->fields()) {
        std::string fname = field->getNameAsString();
        std::shared_ptr<Type> ftype =
            typeTranslator.translate(field->getType());

        fields.push_back(std::make_shared<Field>(fname, ftype));
    }

    uint64_t sizeInBits = astContext->getTypeSize(record->getTypeForDecl());
    assert(sizeInBits % 8 == 0);

    ir.addUnion(name, std::move(fields), sizeInBits / 8, getLocation(record));
}

void TreeVisitor::handleStruct(clang::RecordDecl *record, std::string name) {
    std::string newName = "struct_" + name;

    if (record->hasAttr<clang::PackedAttr>()) {
        llvm::errs() << "Warning: struct " << name << " is packed. "
                     << "Packed structs are not supported by Scala Native. "
                     << "Access to fields will not work correctly.\n";
        llvm::errs().flush();
    }

    std::vector<std::shared_ptr<Field>> fields;
    const clang::ASTRecordLayout &recordLayout =
        astContext->getASTRecordLayout(record);

    bool isBitFieldStruct = false;
    for (const clang::FieldDecl *field : record->fields()) {
        if (field->isBitField()) {
            isBitFieldStruct = true;
        }
        std::shared_ptr<Type> ftype =
            typeTranslator.translate(field->getType());
        uint64_t recordOffsetInBits =
            recordLayout.getFieldOffset(field->getFieldIndex());
        fields.push_back(std::make_shared<Field>(field->getNameAsString(),
                                                 ftype, recordOffsetInBits));
    }

    uint64_t sizeInBits = astContext->getTypeSize(record->getTypeForDecl());
    assert(sizeInBits % 8 == 0);

    ir.addStruct(name, std::move(fields), sizeInBits / 8, getLocation(record),
                 record->hasAttr<clang::PackedAttr>(), isBitFieldStruct);
}

bool TreeVisitor::VisitVarDecl(clang::VarDecl *varDecl) {
    if (!astContext->getSourceManager().isInMainFile(varDecl->getLocation())) {
        /* include variables only from the original header */
        return true;
    }
    if (!varDecl->isThisDeclarationADefinition()) {
        std::string variableName = varDecl->getName().str();
        std::shared_ptr<Type> type =
            typeTranslator.translate(varDecl->getType());
        std::shared_ptr<Variable> variable = ir.addVariable(variableName, type);
        /* check if there is a macro for the variable.
         * Macros were saved by DefineFinder */
        std::string macroName = ir.getDefineForVar(variableName);
        if (!macroName.empty()) {
            ir.addVarDefine(macroName, variable);
        }
    }
    return true;
}

std::shared_ptr<Location> TreeVisitor::getLocation(clang::Decl *decl) {
    clang::SourceManager &sm = astContext->getSourceManager();
    std::string filename = std::string(sm.getFilename(decl->getLocation()));
    char *p = realpath(filename.c_str(), nullptr);
    std::string path;
    if (p) {
        path = p;
        delete[] p;
    } else {
        // TODO: check when it happens
        path = "";
    }

    unsigned lineNumber = sm.getSpellingLineNumber(decl->getLocation());
    return std::make_shared<Location>(path, lineNumber);
}
