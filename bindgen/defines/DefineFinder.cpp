#include "DefineFinder.h"
#include "../ir/types/PrimitiveType.h"

#include <llvm/ADT/APInt.h>
#include <sstream>

DefineFinder::DefineFinder(IR &ir, const clang::CompilerInstance &compiler,
                           clang::Preprocessor &pp)
    : ir(ir), compiler(compiler), pp(pp) {}

void DefineFinder::MacroDefined(const clang::Token &macroNameTok,
                                const clang::MacroDirective *md) {
    clang::SourceManager &sm = compiler.getSourceManager();
    if (!sm.isInMainFile(md->getLocation())) {
        /* include defines only from the original header */
        return;
    }
    if (sm.isWrittenInMainFile(macroNameTok.getLocation()) && md->isDefined() &&
        !md->getMacroInfo()->isFunctionLike()) {
        /* save defines only from the given header.
         *
         * Ignore function-like definitions because they usually are meaningful
         * only inside C functions. */

        std::vector<clang::Token> *tokens = expandDefine(*md);
        if (!tokens) { // there was function-like macro
            return;
        }
        std::string macroName = macroNameTok.getIdentifierInfo()->getName();

        if (tokens->size() == 1 &&
            (*tokens)[0].getKind() == clang::tok::numeric_constant) {
            clang::Token numberToken = (*tokens)[0];
            std::string literal(numberToken.getLiteralData(),
                                numberToken.getLength());
            addNumericConstantDefine(macroName, literal, numberToken);
        } else if (tokens->size() == 2 &&
                   (*tokens)[0].getKind() == clang::tok::minus &&
                   (*tokens)[1].getKind() == clang::tok::numeric_constant) {
            clang::Token numberToken = (*tokens)[1];
            std::string literal(numberToken.getLiteralData(),
                                numberToken.getLength());
            addNumericConstantDefine(macroName, literal, numberToken, false);
        } else if (tokens->size() == 1 &&
                   (*tokens)[0].getKind() == clang::tok::string_literal) {
            clang::Token stringToken = (*tokens)[0];
            std::string literal(stringToken.getLiteralData(),
                                stringToken.getLength());
            ir.addLiteralDefine(
                macroName, "c" + literal,
                std::make_shared<PrimitiveType>("CString"));
        } else if (tokens->size() == 1 &&
                   (*tokens)[0].getKind() == clang::tok::identifier) {
            // token might be a variable
            std::string varName = (*tokens)[0].getIdentifierInfo()->getName();
            ir.addPossibleVarDefine(macroName, varName);
        }
        delete tokens;
    }
}

/**
 * @return array of tokens. Non of the returned tokens is a macro.
 */
std::vector<clang::Token> *
DefineFinder::expandDefine(const clang::MacroDirective &md) {
    auto *expandedTokens = new std::vector<clang::Token>();
    clang::ArrayRef<clang::Token> tokens = md.getMacroInfo()->tokens();
    for (const auto &token : tokens) {
        if (isMacro(token)) {
            if (isFunctionLikeMacro(token)) {
                /* function-like macros are unsupported */
                return nullptr;
            }
            std::vector<clang::Token> *newTokens = expandDefine(
                *pp.getLocalMacroDirective(token.getIdentifierInfo()));
            if (!newTokens) {
                delete expandedTokens;
                return nullptr;
            }
            for (const auto &newToken : *newTokens) {
                (*expandedTokens).push_back(newToken);
            }
            delete newTokens;
        } else {
            (*expandedTokens).push_back(token);
        }
    }
    return expandedTokens;
}

bool DefineFinder::isMacro(const clang::Token &token) {
    return token.isAnyIdentifier() &&
           token.getIdentifierInfo()->hasMacroDefinition();
}

bool DefineFinder::isFunctionLikeMacro(const clang::Token &token) {
    clang::MacroDirective *md =
        pp.getLocalMacroDirective(token.getIdentifierInfo());
    return md->getMacroInfo()->isFunctionLike();
}

void DefineFinder::MacroUndefined(const clang::Token &macroNameTok,
                                  const clang::MacroDefinition &md,
                                  const clang::MacroDirective *undef) {
    if (!undef) {
        /* macro was not defined */
        return;
    }
    clang::SourceManager &sm = compiler.getSourceManager();
    if (sm.isWrittenInMainFile(macroNameTok.getLocation()) &&
        md.getMacroInfo() && !md.getMacroInfo()->isFunctionLike()) {
        std::string macroName = macroNameTok.getIdentifierInfo()->getName();
        ir.removeDefine(macroName);
    }
}

void DefineFinder::addNumericConstantDefine(const std::string &macroName,
                                            std::string literal,
                                            const clang::Token &token,
                                            bool positive) {
    clang::NumericLiteralParser parser(literal, token.getLocation(), pp);
    std::string type;
    std::string scalaLiteral;
    if (parser.isIntegerLiteral()) {
        if (parser.isLongLong) {
            /* literal has `LL` ending. `long long` is represented as `Long`
             * in Scala Native */
            type = "CLongLong";

            /* must fit into Scala integer type */
            if (!integerFitsIntoType<long, unsigned long>(parser, positive)) {
                type.clear();
            }
        } else if (parser.isLong) {
            /* literal has `L` ending */
            type = "CLong";

            /* must fit into Scala integer type */
            if (!integerFitsIntoType<long, unsigned long>(parser, positive)) {
                type.clear();
            }
        } else {
            type = getTypeOfIntegerLiteral(parser, literal, positive);
        }

        if (!type.empty()) {
            scalaLiteral = getDecimalLiteral(parser);
            if (type == "CLong" || type == "CLongLong") {
                scalaLiteral = scalaLiteral + "L";
            }
        }
    } else if (parser.isFloatingLiteral()) {
        if (fitsIntoDouble(parser)) {
            type = "CDouble";
            scalaLiteral = getDoubleLiteral(parser);
        }
    }

    if (!type.empty()) {
        if (!positive) {
            scalaLiteral = "-" + scalaLiteral;
        }
        ir.addLiteralDefine(macroName, scalaLiteral,
                            std::make_shared<PrimitiveType>(type));
    }
}

std::string
DefineFinder::getTypeOfIntegerLiteral(const clang::NumericLiteralParser &parser,
                                      const std::string &literal,
                                      bool positive) {

    if (integerFitsIntoType<int, uint>(parser, positive)) {
        return "CInt";
    } else if (integerFitsIntoType<long, unsigned long>(parser, positive)) {
        return "CLong";
    } else {
        llvm::errs() << "Warning: integer value does not fit into 8 bytes: "
                     << literal << "\n";
        llvm::errs().flush();
        /**
         * `long long` value has mostly the same size as `long`.
         * Moreover in Scala Native the type is represented as `Long`:
         * @code
         * type CLongLong = Long
         * @endcode
         * Therefore the case of `long long` is not considered here.
         */
        return "";
    }
}

template <typename signedT, typename unsignedT>
bool DefineFinder::integerFitsIntoType(clang::NumericLiteralParser parser,
                                       bool positive) {
    /* absolute value of minimum negative number will not fit
     * into (sizeof(signedT) * 8 - 1) bits */
    llvm::APInt uintValue(sizeof(signedT) * 8, 0, false);
    if (parser.GetIntegerValue(uintValue)) {
        /* absolute value of the number does not fit into (sizeof(signedT) * 8)
         * bits */
        return false;
    }
    auto uval = static_cast<unsignedT>(uintValue.getZExtValue());
    if (positive) {
        return uval <=
               static_cast<unsignedT>(std::numeric_limits<signedT>::max());
    } else {
        return uval <=
               static_cast<unsignedT>(-std::numeric_limits<signedT>::min());
    }
}

std::string
DefineFinder::getDecimalLiteral(clang::NumericLiteralParser parser) {
    llvm::APInt val(8 * 8, 0, false);
    parser.GetIntegerValue(val);
    return std::to_string(val.getZExtValue());
}

std::string DefineFinder::getDoubleLiteral(clang::NumericLiteralParser parser) {
    llvm::APFloat val(.0); // double
    parser.GetFloatValue(val);
    std::ostringstream ss;
    ss << val.convertToDouble();
    return ss.str();
}

bool DefineFinder::fitsIntoDouble(clang::NumericLiteralParser parser) {
    llvm::APFloat val(.0); // double
    parser.GetFloatValue(val);
    std::ostringstream ss;
    ss << val.convertToDouble();
    return ss.str() != "inf";
}
