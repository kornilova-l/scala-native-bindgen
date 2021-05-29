#include "TypeDef.h"
#include "../Utils.h"
#include "Struct.h"
#include "Union.h"
#include <sstream>
#include <stdexcept>

TypeDef::TypeDef(std::string name, std::shared_ptr<const Type> type,
                 std::shared_ptr<Location> location)
    : TypeAndName(std::move(name), std::move(type)),
      LocatableType(std::move(location)) {}

std::string
TypeDef::getDefinition(const LocationManager &locationManager) const {
    std::stringstream s;
    s << "  type " << this->str(locationManager) << " = ";
    if (type) {
        s << type->str(locationManager);
    } else {
        s << "CStruct0 // incomplete type";
    }
    s << "\n";
    return s.str();
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
    visitedTypes.pop_back();
    return result;
}

std::string TypeDef::str(const LocationManager &locationManager) const {
    if (hasLocation()) {
        std::shared_ptr<const Location> location = getLocation();
        if (locationManager.isImported(*location)) {
            return locationManager.getImportedType(*location, name);
        }
    }
    return handleReservedWords(replaceChar(name, " ", "_"));
}

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

std::shared_ptr<Location> TypeDef::getLocation() const {
    if (!type) {
        throw std::logic_error(
            "TypeDef::getLocation method should not be called on typedef that "
            "references an opaque type because location cannot be "
            "defined.");
    }
    if (location) {
        /* if typedef is not generated */
        return location;
    }
    auto recordPointer = std::dynamic_pointer_cast<const Record>(type);
    if (recordPointer) {
        return recordPointer->getLocation();
    }
    throw std::logic_error("Generated typedef may reference only records.");
}

bool TypeDef::hasLocation() const { return location || type; }

bool TypeDef::findAllCycles(
    const std::shared_ptr<const Struct> &startStruct, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes) || !type) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = type->findAllCycles(startStruct, cycleNode, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

std::shared_ptr<const Type> TypeDef::unrollTypedefs() const {
    if (isGenerated()) {
        return std::make_shared<TypeDef>(name, type, nullptr);
    }
    return type->unrollTypedefs();
}

std::shared_ptr<const Type>
TypeDef::replaceType(const std::shared_ptr<const Type> &type,
                     const std::shared_ptr<const Type> &replacement) const {
    if (isGenerated()) {
        return std::make_shared<TypeDef>(name, this->type, nullptr);
    }
    if (*this->type == *type) {
        return std::make_shared<TypeDef>(name, replacement, nullptr);
    }
    return std::make_shared<TypeDef>(
        name, this->type->replaceType(type, replacement), nullptr);
}

bool TypeDef::wrapperForOpaqueType() const { return !type; }

bool TypeDef::isGenerated() const {
    return !this->type || isInstanceOf<Struct>(this->type.get()) ||
           isInstanceOf<Union>(this->type.get());
}
