#include "VarDefine.h"

VarDefine::VarDefine(std::string name, std::shared_ptr<Variable> variable)
    : Define(std::move(name)), variable(variable) {}

std::string
VarDefine::getDefinition(const LocationManager &locationManager) const {
    return "  @name(\"" + variable->getName() + "\")\n" + "  val " + name +
           ": " + variable->getType()->str(locationManager) +
           " = extern\n";
}

bool VarDefine::hasIllegalUsageOfOpaqueType() const {
    return variable->hasIllegalUsageOfOpaqueType();
}
