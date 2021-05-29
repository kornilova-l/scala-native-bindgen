#include "ArrayType.h"
#include "../../Utils.h"
#include "../Union.h"

ArrayType::ArrayType(std::shared_ptr<const Type> elementsType, uint64_t size)
    : size(size), elementsType(std::move(elementsType)) {}

std::string ArrayType::str(const LocationManager &locationManager) const {
    return "CArray[" + elementsType->str(locationManager) + ", " +
           uint64ToScalaNat(size) + "]";
}

bool ArrayType::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = *elementsType == *type ||
                  elementsType->usesType(type, stopOnTypeDefs, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

bool ArrayType::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<ArrayType>(&other) && !isInstanceOf<Union>(&other)) {
        auto *arrayType = dynamic_cast<const ArrayType *>(&other);
        if (size != arrayType->size) {
            return false;
        }
        return *elementsType == *arrayType->elementsType;
    }
    return false;
}

std::shared_ptr<const Type> ArrayType::unrollTypedefs() const {
    return std::make_shared<ArrayType>(elementsType->unrollTypedefs(), size);
}

std::shared_ptr<const Type>
ArrayType::replaceType(const std::shared_ptr<const Type> &type,
                       const std::shared_ptr<const Type> &replacement) const {

    if (*elementsType == *replacement) {
        return std::make_shared<ArrayType>(replacement, size);
    }
    return std::make_shared<ArrayType>(
        elementsType->replaceType(type, replacement), size);
}
