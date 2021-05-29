#include "IR.h"
#include "../Utils.h"
#include <sstream>

IR::IR(std::string libName, std::string linkName, std::string objectName,
       std::string packageName, const LocationManager &locationManager)
    : libName(std::move(libName)), linkName(std::move(linkName)),
      objectName(std::move(objectName)), locationManager(locationManager),
      packageName(std::move(packageName)) {}

void IR::addFunction(std::string name,
                     std::vector<std::shared_ptr<Parameter>> parameters,
                     std::shared_ptr<Type> retType, bool isVariadic) {
    functions.push_back(std::make_shared<Function>(name, std::move(parameters),
                                                   retType, isVariadic));
}

std::shared_ptr<TypeDef> IR::addTypeDef(std::string name,
                                        std::shared_ptr<Type> type,
                                        std::shared_ptr<Location> location) {
    typeDefs.push_back(
        std::make_shared<TypeDef>(std::move(name), type, std::move(location)));
    return typeDefs.back();
}

std::shared_ptr<Enum> IR::addEnum(std::string name, const std::string &type,
                                  std::vector<Enumerator> enumerators,
                                  std::shared_ptr<Location> location) {
    std::shared_ptr<Enum> e = std::make_shared<Enum>(
        std::move(name), type, std::move(enumerators), std::move(location));
    enums.push_back(e);
    return e;
}

std::shared_ptr<TypeDef>
IR::addStruct(std::string name, std::vector<std::shared_ptr<Field>> fields,
              uint64_t typeSize, std::shared_ptr<Location> location,
              bool isPacked, bool isBitField) {
    std::shared_ptr<Struct> s =
        std::make_shared<Struct>(name, std::move(fields), typeSize,
                                 std::move(location), isPacked, isBitField);
    structs.push_back(s);
    std::shared_ptr<TypeDef> typeDef = getTypeDefWithName("struct " + name);
    if (typeDef) {
        /* the struct type used to be opaque type, typeDef contains nullptr */
        typeDef.get()->setType(s);
        return typeDef;
    } else {
        typeDefs.push_back(s->generateTypeDef());
        return typeDefs.back();
    }
}

std::shared_ptr<TypeDef>
IR::addUnion(std::string name, std::vector<std::shared_ptr<Field>> fields,
             uint64_t maxSize, std::shared_ptr<Location> location) {
    std::shared_ptr<Union> u = std::make_shared<Union>(
        name, std::move(fields), maxSize, std::move(location));
    unions.push_back(u);
    std::shared_ptr<TypeDef> typeDef = getTypeDefWithName("union " + name);
    if (typeDef) {
        /* the union type used to be opaque type, typeDef contains nullptr */
        typeDef.get()->setType(u);
        return typeDef;
    } else {
        typeDefs.push_back(u->generateTypeDef());
        return typeDefs.back();
    }
}

void IR::addLiteralDefine(std::string name, std::string literal,
                          std::shared_ptr<Type> type) {
    literalDefines.push_back(std::make_shared<LiteralDefine>(
        std::move(name), std::move(literal), type));
}

void IR::addPossibleVarDefine(const std::string &macroName,
                              const std::string &varName) {
    possibleVarDefines.push_back(
        std::make_shared<PossibleVarDefine>(macroName, varName));
}

void IR::addVarDefine(std::string name, std::shared_ptr<Variable> variable) {
    varDefines.push_back(
        std::make_shared<VarDefine>(std::move(name), variable));
}

bool IR::libObjEmpty() const {
    return functions.empty() && !shouldOutputType(typeDefs) &&
           !shouldOutputType(structs) && !shouldOutputType(unions) &&
           varDefines.empty() && variables.empty() &&
           !shouldOutputType(enums) && literalDefines.empty();
}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const IR &ir) {
    assert(ir.generated); // typedefs were generated

    if (!ir.packageName.empty()) {
        s << "package " << ir.packageName << "\n\n";
    }

    if (ir.libObjEmpty()) {
        return s;
    }

    s << "import scala.scalanative.unsigned._\n"
      << "import scala.scalanative.unsafe._\n\n";

    if (!ir.functions.empty() || !ir.varDefines.empty() ||
        !ir.variables.empty()) {
        if (!ir.linkName.empty()) {
            s << "@link(\"" << ir.linkName << "\")\n";
        }
        s << "@extern\n";
    }
    s << "object " << handleReservedWords(ir.objectName) << " {\n";

    std::vector<std::shared_ptr<const Type>> visitedTypes;

    for (const auto &e : ir.enums) {
        visitedTypes.clear();
        if (ir.shouldOutput(e, visitedTypes)) {
            s << e->getDefinition();
            s << e->getEnumerators() << "\n";
        }
    }

    for (const auto &typeDef : ir.typeDefs) {
        visitedTypes.clear();
        if (ir.shouldOutputTypeDef(typeDef, visitedTypes)) {
            s << typeDef->getDefinition(ir.locationManager);
        } else if (typeDef->hasLocation() &&
                   isAliasForOpaqueType(typeDef.get()) &&
                   ir.locationManager.inMainFile(*typeDef->getLocation())) {
            llvm::errs() << "Warning: type alias " + typeDef->getName()
                         << " is skipped because it is an unused alias for "
                            "incomplete type."
                         << "\n";
            llvm::errs().flush();
        }
    }

    for (const auto &variable : ir.variables) {
        if (!variable->hasIllegalUsageOfOpaqueType()) {
            s << variable->getDefinition(ir.locationManager);
        } else {
            llvm::errs() << "Error: Variable " << variable->getName()
                         << " is skipped because it has incomplete type.\n";
        }
    }

    for (const auto &varDefine : ir.varDefines) {
        if (!varDefine->hasIllegalUsageOfOpaqueType()) {
            s << varDefine->getDefinition(ir.locationManager);
        } else {
            llvm::errs() << "Error: Variable alias " << varDefine->getName()
                         << " is skipped because it has incomplete type.\n";
            llvm::errs().flush();
        }
    }

    for (const auto &func : ir.functions) {
        if (!func->isLegalScalaNativeFunction()) {
            llvm::errs() << "Warning: Function " << func->getName()
                         << " is skipped because Scala Native does not support "
                            "passing structs and arrays by value.\n";
            llvm::errs().flush();
        } else {
            s << func->getDefinition(ir.locationManager);
        }
    }

    if (!ir.literalDefines.empty()) {
        s << "\n  object defines {\n";
        for (const auto &literalDefine : ir.literalDefines) {
            s << literalDefine->getDefinition(ir.locationManager);
        }
        s << "  }\n";
    }

    if (ir.hasHelperMethods()) {
        s << "\n  object implicits {\n" << ir.getHelperMethods() << "  }\n";
    }

    for (const auto &str : ir.structs) {
        visitedTypes.clear();
        if (ir.shouldOutput(str, visitedTypes) && str->hasHelperMethods()) {
            s << "\n" << str->getConstructorHelper(ir.locationManager);
        }
    }

    s << "}\n\n";

    return s;
}

void IR::generate(const std::string &excludePrefix) {
    if (!generated) {
        setScalaNames();
        filterDeclarations(excludePrefix);
        generated = true;
    }
}

bool IR::hasHelperMethods() const {
    std::vector<std::shared_ptr<const Type>> visitedTypes;
    for (const auto &u : unions) {
        visitedTypes.clear();
        if (shouldOutput(u, visitedTypes) && u->hasHelperMethods()) {
            return true;
        }
    }

    for (const auto &s : structs) {
        visitedTypes.clear();
        if (shouldOutput(s, visitedTypes) && s->hasHelperMethods()) {
            return true;
        }
    }
    return false;
}

void IR::filterDeclarations(const std::string &excludePrefix) {
    if (excludePrefix.empty()) {
        return;
    }

    filterTypeDefs(excludePrefix);

    filterByPrefix(functions, excludePrefix);

    filterByPrefix(literalDefines, excludePrefix);

    filterByPrefix(varDefines, excludePrefix);

    filterByPrefix(variables, excludePrefix);
}

void IR::filterTypeDefs(const std::string &excludePrefix) {
    for (auto it = typeDefs.begin(); it != typeDefs.end();) {
        std::shared_ptr<TypeDef> typeDef = *it;
        if (startsWith(typeDef->getName(), excludePrefix) &&
            typeIsUsedOnlyInTypeDefs(typeDef)) {
            /* remove this typedef and replace aliases with actual type */
            replaceTypeInTypeDefs(typeDef, typeDef->getType());
            it = typeDefs.erase(it);
        } else {
            ++it;
        }
    }
}

void IR::replaceTypeInTypeDefs(std::shared_ptr<const Type> oldType,
                               std::shared_ptr<const Type> newType) {
    for (auto &typeDef : typeDefs) {
        if (typeDef->getType() == oldType) {
            typeDef->setType(newType);
        }
    }
}

template <typename T>
bool IR::isTypeUsed(const std::vector<T> &declarations,
                    std::shared_ptr<const Type> type,
                    bool stopOnTypeDefs) const {
    std::vector<std::shared_ptr<const Type>> visitedTypes;
    for (const auto &decl : declarations) {
        visitedTypes.clear();
        if (decl->usesType(type, stopOnTypeDefs, visitedTypes)) {
            return true;
        }
    }
    return false;
}

bool IR::typeIsUsedOnlyInTypeDefs(
    const std::shared_ptr<const Type> &type) const {
    /* varDefines are not checked here because they are simply
     * aliases for variables.*/
    return !(
        isTypeUsed(functions, type, true) || isTypeUsed(structs, type, true) ||
        isTypeUsed(unions, type, true) || isTypeUsed(variables, type, true) ||
        isTypeUsed(literalDefines, type, true));
}

bool IR::isTypeUsed(
    const std::shared_ptr<const Type> &type,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(type.get(), visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(type);
    if (isTypeUsed(functions, type, true) ||
        isTypeUsed(variables, type, true) ||
        isTypeUsed(literalDefines, type, true)) {
        return true;
    }
    /* type is used if there exists another type that is used and that
     * references this type */
    std::vector<std::shared_ptr<const Type>> visitedTypesInner;
    for (const auto &typeDef : typeDefs) {
        visitedTypesInner.clear();
        if (typeDef->usesType(type, false, visitedTypesInner)) {
            if (shouldOutputTypeDef(typeDef, visitedTypes)) {
                return true;
            }
        }
    }
    for (const auto &s : structs) {
        /* stopOnTypeDefs parameter is true because because typedefs were
         * checked */
        visitedTypesInner.clear();
        if (s->usesType(type, true, visitedTypesInner)) {
            if (shouldOutput(s, visitedTypes)) {
                return true;
            }
        }
    }
    for (const auto &u : unions) {
        /* stopOnTypeDefs parameter is true because because typedefs were
         * checked */
        visitedTypesInner.clear();
        if (u->usesType(type, true, visitedTypesInner)) {
            if (shouldOutput(u, visitedTypes)) {
                return true;
            }
        }
    }
    return false;
}

void IR::setScalaNames() {
    /* Renaming according to Scala naming conventions
     * should happen here */

    for (auto &function : functions) {
        if (function->getName() == "native") {
            std::string scalaName = "nativeFunc";
            int i = 0;
            while (existsFunctionWithName(scalaName)) {
                scalaName = "nativeFunc" + std::to_string(i++);
            }
            function->setScalaName(scalaName);
        }
    }
}

bool IR::existsFunctionWithName(std::string functionName) {
    for (const auto &function : functions) {
        if (function->getName() == functionName) {
            return true;
        }
    }
    return false;
}

void IR::removeDefine(const std::string &name) {
    filterByName(literalDefines, name);
    filterByName(possibleVarDefines, name);
    filterByName(varDefines, name);
}

template <typename T>
void IR::filterByPrefix(std::vector<T> &declarations,
                        const std::string &excludePrefix) {
    for (auto it = declarations.begin(); it != declarations.end();) {
        T declaration = *it;
        if (startsWith(declaration->getName(), excludePrefix)) {
            it = declarations.erase(it);
        } else {
            it++;
        }
    }
}

template <typename T>
void IR::filterByName(std::vector<T> &declarations, const std::string &name) {
    for (auto it = declarations.begin(); it != declarations.end();) {
        T declaration = *it;
        if (declaration->getName() == name) {
            it = declarations.erase(it);
        } else {
            it++;
        }
    }
}

std::string IR::getDefineForVar(const std::string &varName) const {
    for (const auto &varDefine : possibleVarDefines) {
        if (varDefine->getVariableName() == varName) {
            return varDefine->getName();
        }
    }
    return "";
}

std::shared_ptr<Variable> IR::addVariable(const std::string &name,
                                          std::shared_ptr<Type> type) {
    std::shared_ptr<Variable> variable = std::make_shared<Variable>(name, type);
    variables.push_back(variable);
    return variable;
}

std::shared_ptr<TypeDef> IR::getTypeDefWithName(const std::string &name) const {
    /* nullptr is returned in 2 cases:
     * 1. TypeTranslator translates opaque struct/union type for which TypeDef
     *    was not created.
     * 2. TreeVisitor visits struct/union declaration and it checks whether a
     *    TypeDef already exists for it.*/
    return getDeclarationWithName(typeDefs, name);
}

std::shared_ptr<Enum> IR::getEnumWithName(const std::string &name) const {
    return getDeclarationWithName(enums, name);
}

template <typename T>
T IR::getDeclarationWithName(const std::vector<T> &declarations,
                             const std::string &name) const {
    for (auto it = declarations.begin(), end = declarations.end(); it != end;
         ++it) {
        T declaration = (*it);
        if (declaration->getName() == name) {
            return declaration;
        }
    }
    return nullptr;
}

IR::~IR() {
    functions.clear();
    typeDefs.clear();
    structs.clear();
    unions.clear();
    enums.clear();
    literalDefines.clear();
    possibleVarDefines.clear();
    variables.clear();
    varDefines.clear();
}

template <typename T>
bool IR::shouldOutputType(
    const std::vector<std::shared_ptr<T>> &declarations) const {
    std::vector<std::shared_ptr<const Type>> visitedTypes;
    for (const auto &declaration : declarations) {
        visitedTypes.clear();
        auto typeDefPointer =
            std::dynamic_pointer_cast<const TypeDef>(declaration);
        if (typeDefPointer) {
            if (shouldOutputTypeDef(typeDefPointer, visitedTypes)) {
                return true;
            }
        } else {
            if (shouldOutput(declaration, visitedTypes)) {
                return true;
            }
        }
    }
    return false;
}

bool IR::shouldOutput(
    const std::shared_ptr<const LocatableType> &type,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (locationManager.isImported(*type->getLocation())) {
        return false;
    }
    if (isTypeUsed(type, visitedTypes)) {
        return true;
    }
    /* remove unused types from included files */
    return locationManager.inMainFile(*type->getLocation());
}

bool IR::shouldOutputTypeDef(
    const std::shared_ptr<const TypeDef> &typeDef,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (isTypeUsed(typeDef, visitedTypes)) {
        if (typeDef->wrapperForOpaqueType()) {
            /* it is not possible to get location of this typedef
             * so the typedef cannot be imported from other bindings */
            return true;
        }
        return !locationManager.isImported(*typeDef->getLocation());
    }
    if (isAliasForOpaqueType(typeDef.get())) {
        /* it does not matter where unused alias for opaque type is located */
        return false;
    }
    /* remove unused types from included files */
    return locationManager.inMainFile(*typeDef->getLocation());
}

std::string IR::getHelperMethods() const {
    std::stringstream s;
    std::vector<std::shared_ptr<const Type>> visitedTypes;

    std::string sep = "";

    for (const auto &st : structs) {
        visitedTypes.clear();
        if (shouldOutput(st, visitedTypes) && st->hasHelperMethods()) {
            s << sep << st->generateHelperClass(locationManager);
            sep = "\n";
        }
    }

    for (const auto &u : unions) {
        visitedTypes.clear();
        if (shouldOutput(u, visitedTypes) && u->hasHelperMethods()) {
            s << sep << u->generateHelperClass(locationManager);
        }
        sep = "\n";
    }
    return s.str();
}
