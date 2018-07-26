#include "LiteralDefine.h"

LiteralDefine::LiteralDefine(std::string name, std::string literal,
                             std::shared_ptr<const Type> type)
    : Define(std::move(name)), literal(std::move(literal)), type(type) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s,
                              const LiteralDefine &literalDefine) {
    s << "  val " << literalDefine.name << ": " << literalDefine.type->str()
      << " = " << literalDefine.literal << "\n";
    return s;
}

bool LiteralDefine::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (*this->type == *type) {
        return true;
    }
    visitedTypes.clear();
    return this->type->usesType(type, stopOnTypeDefs, visitedTypes);
}
