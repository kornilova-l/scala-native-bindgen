#include "PointerType.h"
#include "../../Utils.h"

PointerType::PointerType(std::shared_ptr<const Type> type)
    : type(std::move(type)) {}

std::string PointerType::str(const LocationManager &locationManager) const {
    return "Ptr[" + type->str(locationManager) + "]";
}

bool PointerType::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = *this->type == *type ||
                  this->type->usesType(type, stopOnTypeDefs, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

bool PointerType::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<PointerType>(&other)) {
        auto *pointerType = dynamic_cast<const PointerType *>(&other);
        return *type == *pointerType->type;
    }
    return false;
}

bool PointerType::findAllCycles(
    const std::shared_ptr<const Struct> &startStruct, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result =
        this->type->findAllCycles(startStruct, cycleNode, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

std::shared_ptr<const Type> PointerType::unrollTypedefs() const {
    return std::make_shared<PointerType>(type->unrollTypedefs());
}

std::shared_ptr<const Type>
PointerType::replaceType(const std::shared_ptr<const Type> &type,
                         const std::shared_ptr<const Type> &replacement) const {
    if (*this->type == *type) {
        return std::make_shared<PointerType>(replacement);
    }
    return std::make_shared<PointerType>(
        this->type->replaceType(type, replacement));
}
