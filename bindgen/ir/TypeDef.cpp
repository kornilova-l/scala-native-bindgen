#include "TypeDef.h"
#include "../Utils.h"
#include "Enum.h"
#include "Struct.h"

TypeDef::TypeDef(std::string name, std::shared_ptr<const Type> type,
                 std::shared_ptr<Location> location)
    : TypeAndName(std::move(name), std::move(type)),
      location(std::move(location)) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const TypeDef &typeDef) {
    s << "  type " << handleReservedWords(typeDef.name) << " = ";
    if (typeDef.type) {
        s << typeDef.getType()->str();
    } else {
        s << "native.CStruct0 // incomplete type";
    }
    s << "\n";
    return s;
}

bool TypeDef::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (stopOnTypeDefs) {
        return false;
    }
    if (!this->type) {
        return false;
    }
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = *this->type == *type ||
                  this->type->usesType(type, stopOnTypeDefs, visitedTypes);
    if (!result) {
        /* current TypeDef instance should not be in the path to search
         * type */
        visitedTypes.pop_back();
    }
    return result;
}

std::string TypeDef::str() const { return handleReservedWords(name); }

bool TypeDef::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<TypeDef>(&other)) {
        auto *typDef = dynamic_cast<const TypeDef *>(&other);
        if (name != typDef->name) {
            return false;
        }
        return *type == *typDef->type;
    }
    return false;
}

std::shared_ptr<Location> TypeDef::getLocation() const { return location; }

bool TypeDef::findAllCycles(
    const StructOrUnion *startStructOrUnion, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes) || !type) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result =
        type->findAllCycles(startStructOrUnion, cycleNode, visitedTypes);
    visitedTypes.pop_back();
    return result;
}
