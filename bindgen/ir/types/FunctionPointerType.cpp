#include "FunctionPointerType.h"
#include "../../Utils.h"
#include <sstream>

FunctionPointerType::FunctionPointerType(
    std::shared_ptr<const Type> returnType,
    std::vector<std::shared_ptr<const Type>> &parametersTypes, bool isVariadic)
    : returnType(std::move(returnType)), parametersTypes(parametersTypes),
      isVariadic(isVariadic) {}

std::string
FunctionPointerType::str(const LocationManager &locationManager) const {
    std::stringstream ss;
    ss << "CFuncPtr" << parametersTypes.size() << "[";

    for (const auto &parameterType : parametersTypes) {
        ss << parameterType->str(locationManager) << ", ";
    }

    if (isVariadic) {
        ss << "CVarArg, ";
    }
    ss << returnType->str(locationManager) << "]";
    return ss.str();
}

bool FunctionPointerType::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());

    if (*returnType == *type ||
        returnType->usesType(type, stopOnTypeDefs, visitedTypes)) {
        visitedTypes.pop_back();
        return true;
    }

    for (const auto &parameterType : parametersTypes) {
        if (*parameterType == *type ||
            parameterType->usesType(type, stopOnTypeDefs, visitedTypes)) {
            visitedTypes.pop_back();
            return true;
        }
    }
    visitedTypes.pop_back();
    return false;
}

bool FunctionPointerType::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<const FunctionPointerType>(&other)) {
        auto *functionPointerType =
            dynamic_cast<const FunctionPointerType *>(&other);
        if (isVariadic != functionPointerType->isVariadic) {
            return false;
        }
        if (*returnType != *functionPointerType->returnType) {
            return false;
        }
        if (parametersTypes.size() !=
            functionPointerType->parametersTypes.size()) {
            return false;
        }
        for (size_t i = 0; i < parametersTypes.size(); i++) {
            if (*parametersTypes[i] !=
                *functionPointerType->parametersTypes[i]) {
                return false;
            }
        }
        return true;
    }
    return false;
}

bool FunctionPointerType::findAllCycles(
    const std::shared_ptr<const Struct> &startStruct, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {

    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());

    bool foundCycle = false;

    if (returnType->findAllCycles(startStruct, cycleNode, visitedTypes)) {
        foundCycle = true;
    }

    for (const auto &parameterType : parametersTypes) {
        if (parameterType->findAllCycles(startStruct, cycleNode,
                                         visitedTypes)) {
            foundCycle = true;
        }
    }
    visitedTypes.pop_back();
    return foundCycle;
}

std::shared_ptr<const Type> FunctionPointerType::unrollTypedefs() const {
    std::vector<std::shared_ptr<const Type>> unrolledParameterTypes;
    for (const auto &parameterType : parametersTypes) {
        unrolledParameterTypes.push_back(parameterType->unrollTypedefs());
    }
    return std::make_shared<FunctionPointerType>(
        returnType->unrollTypedefs(), unrolledParameterTypes, isVariadic);
}

std::shared_ptr<const Type> FunctionPointerType::replaceType(
    const std::shared_ptr<const Type> &type,
    const std::shared_ptr<const Type> &replacement) const {
    std::shared_ptr<const Type> newReturnType = returnType;
    if (*returnType == *type) {
        newReturnType = replacement;
    }
    std::vector<std::shared_ptr<const Type>> newParametersTypes;
    for (const auto &parameterType : parametersTypes) {
        if (*parameterType == *type) {
            newParametersTypes.push_back(replacement);
        } else {
            newParametersTypes.push_back(parameterType);
        }
    }

    return std::make_shared<FunctionPointerType>(
        newReturnType, newParametersTypes, isVariadic);
}
