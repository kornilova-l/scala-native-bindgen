#include "Enum.h"
#include <sstream>

Enumerator::Enumerator(std::string name, int64_t value)
    : name(std::move(name)), value(value) {}

std::string Enumerator::getName() { return name; }

int64_t Enumerator::getValue() { return value; }

Enum::Enum(std::string name, std::string type,
           std::vector<Enumerator> enumerators,
           std::shared_ptr<Location> location)
    : PrimitiveType(std::move(type)), LocatableType(std::move(location)),
      name(std::move(name)), enumerators(std::move(enumerators)) {}

std::string Enum::getEnumerators() const {
    std::stringstream s;
    std::string typeCastSuffix = getTypeCastSuffix();
    std::string type = getTypeAlias();
    s << "  object " << type << " {\n";
    for (auto enumerator : enumerators) {
        s << "    final val " << enumerator.getName();
        s << ": " << type << " = " << std::to_string(enumerator.getValue())
          << typeCastSuffix << "\n";
    }
    s << "  }\n";
    return s.str();
}

std::string Enum::getTypeCastSuffix() const {
    std::string primitiveType = PrimitiveType::getType();
    if (primitiveType == "CLong") {
        return "L";
    } else if (primitiveType == "CUnsignedInt") {
        return ".toUInt";
    } else if (primitiveType == "CUnsignedLong") {
        return "L.toULong";
    }
    return "";
}

std::string Enum::str(const LocationManager &locationManager) const {
    if (locationManager.isImported(*location)) {
        return locationManager.getImportedType(*location, getTypeAlias());
    }
    return getTypeAlias();
}

std::string Enum::getTypeAlias() const { return "enum_" + name; }

std::string Enum::getDefinition() const {
    return "  type " + getTypeAlias() + " = " + PrimitiveType::str() + "\n";
}

std::string Enum::getName() const { return name; }
