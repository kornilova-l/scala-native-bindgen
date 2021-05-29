#include "Union.h"
#include "../Utils.h"
#include "Struct.h"
#include "types/ArrayType.h"
#include "types/FunctionPointerType.h"
#include "types/PointerType.h"
#include "types/PrimitiveType.h"
#include <sstream>

Union::Union(std::string name, std::vector<std::shared_ptr<Field>> fields,
             uint64_t maxSize, std::shared_ptr<Location> location)
    : Record(std::move(name), std::move(fields), std::move(location)),
      ArrayType(std::make_shared<PrimitiveType>("Byte"), maxSize) {}

std::shared_ptr<TypeDef> Union::generateTypeDef() {
    return std::make_shared<TypeDef>(getTypeName(), shared_from_this(),
                                     nullptr);
}

std::string
Union::generateHelperClass(const LocationManager &locationManager) const {
    assert(hasHelperMethods());
    std::stringstream s;
    std::string type = replaceChar(getTypeName(), " ", "_");
    s << "    implicit class " << type << "_pos"
      << "(val p: Ptr[" << type << "]) extends AnyVal {\n";
    for (const auto &field : fields) {
        if (!field->getName().empty()) {
            s << generateGetter(field, locationManager);
            s << generateSetter(field, locationManager);
        }
    }
    s << "    }\n";
    return s.str();
}

std::string Union::getTypeName() const { return "union " + name; }

bool Union::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    auto *u = dynamic_cast<const Union *>(&other);
    if (u) {
        /* unions have unique names */
        return name == u->name;
    }
    return false;
}

bool Union::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {

    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());

    if (ArrayType::usesType(type, stopOnTypeDefs, visitedTypes)) {
        visitedTypes.pop_back();
        return true;
    }
    visitedTypes.pop_back();

    return Record::usesType(type, stopOnTypeDefs, visitedTypes);
}

std::string
Union::generateGetter(const std::shared_ptr<Field> &field,
                      const LocationManager &locationManager) const {
    std::string getter = handleReservedWords(field->getName());
    std::string ftype = field->getType()->str(locationManager);
    return "      def " + getter + ": Ptr[" + ftype +
           "] = p.asInstanceOf[Ptr[" + ftype + "]]\n";
}

std::string
Union::generateSetter(const std::shared_ptr<Field> &field,
                      const LocationManager &locationManager) const {
    std::string setter = handleReservedWords(field->getName(), "_=");
    std::string ftype = field->getType()->str(locationManager);
    if (isAliasForType<ArrayType>(field->getType().get()) ||
        isAliasForType<Struct>(field->getType().get())) {
        return "      def " + setter + "(value: Ptr[" + ftype +
               "]): Unit = !p.asInstanceOf[Ptr[" + ftype + "]] = value\n";
    }
    return "      def " + setter + "(value: " + ftype +
           "): Unit = !p.asInstanceOf[Ptr[" + ftype + "]] = value\n";
}
