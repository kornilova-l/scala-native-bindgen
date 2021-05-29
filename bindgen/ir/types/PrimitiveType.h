#ifndef SCALA_NATIVE_BINDGEN_SIMPLETYPE_H
#define SCALA_NATIVE_BINDGEN_SIMPLETYPE_H

#include "Type.h"
#include <string>

/**
 * For example CInt
 */
class PrimitiveType : virtual public Type {
  public:
    explicit PrimitiveType(std::string type);

    std::string getType() const;

    bool usesType(
        const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
        std::vector<std::shared_ptr<const Type>> &visitedTypes) const override;

    std::string str() const;

    std::string str(const LocationManager &locationManager) const override;

    bool operator==(const Type &other) const override;

    std::shared_ptr<const Type> unrollTypedefs() const override;

    std::shared_ptr<const Type>
    replaceType(const std::shared_ptr<const Type> &type,
                const std::shared_ptr<const Type> &replacement) const override;

  private:
    std::string type;
};

#endif // SCALA_NATIVE_BINDGEN_SIMPLETYPE_H
