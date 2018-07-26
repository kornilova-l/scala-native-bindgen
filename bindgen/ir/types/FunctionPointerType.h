#ifndef SCALA_NATIVE_BINDGEN_FUNCTIONPOINTERTYPE_H
#define SCALA_NATIVE_BINDGEN_FUNCTIONPOINTERTYPE_H

#include "Type.h"
#include <vector>

class FunctionPointerType
    : public Type,
      public std::enable_shared_from_this<FunctionPointerType> {
  public:
    FunctionPointerType(
        std::shared_ptr<const Type> returnType,
        std::vector<std::shared_ptr<const Type>> &parametersTypes,
        bool isVariadic);

    bool usesType(
        const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
        std::vector<std::shared_ptr<const Type>> &visitedTypes) const override;

    std::string str() const override;

    bool operator==(const Type &other) const override;

  private:
    std::shared_ptr<const Type> returnType;
    std::vector<std::shared_ptr<const Type>> parametersTypes;
    bool isVariadic;
};

#endif // SCALA_NATIVE_BINDGEN_FUNCTIONPOINTERTYPE_H
