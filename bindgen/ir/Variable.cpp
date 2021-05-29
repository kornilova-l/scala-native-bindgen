#include "Variable.h"
#include "../Utils.h"

Variable::Variable(const std::string &name, std::shared_ptr<const Type> type)
    : TypeAndName(name, type) {}

std::string
Variable::getDefinition(const LocationManager &locationManager) const {
    return "  val " + name + ": " + type->str(locationManager) +
           " = extern\n";
}

bool Variable::hasIllegalUsageOfOpaqueType() const {
    return isAliasForOpaqueType(type.get());
}
