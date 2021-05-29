#include "Struct.h"
#include "../Utils.h"
#include "Union.h"
#include "types/ArrayType.h"
#include "types/FunctionPointerType.h"
#include "types/PointerType.h"
#include "types/PrimitiveType.h"
#include <sstream>

Struct::Struct(std::string name, std::vector<std::shared_ptr<Field>> fields,
               uint64_t typeSize, std::shared_ptr<Location> location,
               bool isPacked, bool isBitField)
    : Record(std::move(name), std::move(fields), std::move(location)),
      typeSize(typeSize), isPacked(isPacked), hasBitField(isBitField) {}

std::shared_ptr<TypeDef> Struct::generateTypeDef() {
    if (isRepresentedAsStruct()) {
        return std::make_shared<TypeDef>(getTypeName(), shared_from_this(),
                                         nullptr);
    } else {
        // There is no easy way to represent it as a struct in scala native,
        // have to represent it as an array and then Add helpers to help with
        // its manipulation
        return std::make_shared<TypeDef>(
            getTypeName(),
            std::make_shared<ArrayType>(std::make_shared<PrimitiveType>("Byte"),
                                        typeSize),
            location);
    }
}

std::string
Struct::generateHelperClass(const LocationManager &locationManager) const {
    assert(hasHelperMethods());
    std::stringstream s;
    std::string type = replaceChar(getTypeName(), " ", "_");
    s << "    implicit class " << type << "_ops(val p: Ptr[" << type
      << "])"
      << " extends AnyVal {\n";
    if (isRepresentedAsStruct()) {
        s << generateHelperClassMethodsForStructRepresentation(locationManager);
    } else {
        s << generateHelperClassMethodsForArrayRepresentation(locationManager);
    }
    s << "    }\n";

    return s.str();
}

bool Struct::hasHelperMethods() const {
    if (hasBitField) {
        return false;
    }
    if (!isRepresentedAsStruct()) {
        return !fields.empty();
    }
    return !isPacked && !fields.empty();
}

std::string Struct::generateHelperClassMethodsForStructRepresentation(
    const LocationManager &locationManager) const {
    std::stringstream s;
    for (unsigned fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
        if (!fields[fieldIndex]->getName().empty()) {
            s << generateGetterForStructRepresentation(fieldIndex,
                                                       locationManager);
            s << generateSetterForStructRepresentation(fieldIndex,
                                                       locationManager);
        }
    }
    return s.str();
}

std::string Struct::generateHelperClassMethodsForArrayRepresentation(
    const LocationManager &locationManager) const {
    std::stringstream s;
    for (unsigned fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
        if (!fields[fieldIndex]->getName().empty()) {
            s << generateGetterForArrayRepresentation(fieldIndex,
                                                      locationManager);
            s << generateSetterForArrayRepresentation(fieldIndex,
                                                      locationManager);
        }
    }
    return s.str();
}

std::string Struct::getTypeName() const { return "struct " + name; }

std::string Struct::str(const LocationManager &locationManager) const {
    std::stringstream ss;
    ss << "CStruct" << std::to_string(fields.size()) << "[";

    std::string sep = "";
    for (const auto &field : fields) {
        ss << sep;
        std::vector<std::shared_ptr<const Struct>>
            structTypesThatShouldBeReplaced = shouldFieldBreakCycle(field);
        if (structTypesThatShouldBeReplaced.empty()) {
            ss << field->getType()->str(locationManager);
        } else {
            /* field type is changed to avoid cyclic types in generated code */
            std::shared_ptr<const Type> typeReplacement = getTypeReplacement(
                field->getType(), structTypesThatShouldBeReplaced);
            ss << typeReplacement->str(locationManager);
        }
        sep = ", ";
    }

    ss << "]";
    return ss.str();
}

bool Struct::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    auto *s = dynamic_cast<const Struct *>(&other);
    if (s) {
        /* structs have unique names */
        return name == s->name;
    }
    return false;
}

std::string Struct::generateSetterForStructRepresentation(
    unsigned fieldIndex, const LocationManager &locationManager) const {
    std::shared_ptr<Field> field = fields[fieldIndex];
    std::string setter = handleReservedWords(field->getName(), "_=");
    std::string parameterType =
        wrapArrayOrRecordInPointer(field->getType())->str(locationManager);
    std::string value = "value";
    std::vector<std::shared_ptr<const Struct>> structTypesThatShouldBeReplaced =
        shouldFieldBreakCycle(field);
    if (!structTypesThatShouldBeReplaced.empty()) {
        /* field type is changed to avoid cyclic types in generated code */
        std::shared_ptr<const Type> typeReplacement = getTypeReplacement(
            field->getType(), structTypesThatShouldBeReplaced);
        value = value + ".asInstanceOf[" + typeReplacement->str(locationManager) + "]";
    } else if (isArrayOrRecord(field->getType())) {
        //value = "!" + value;
    }
    std::stringstream s;
    s << "      def " << setter << "(value: " + parameterType + "): Unit = p._"
      << std::to_string(fieldIndex + 1) << " = " << value << "\n";
    return s.str();
}

std::string Struct::generateGetterForStructRepresentation(
    unsigned fieldIndex, const LocationManager &locationManager) const {
    std::shared_ptr<Field> field = fields[fieldIndex];
    std::string getter = handleReservedWords(field->getName());
    std::string returnType =
        wrapArrayOrRecordInPointer(field->getType())->str(locationManager);
    std::string methodBody = "p._" + std::to_string(fieldIndex + 1);
    if (!isArrayOrRecord(field->getType())) {
        // methodBody = "!" + methodBody;
        if (!shouldFieldBreakCycle(field).empty()) {
            /* field type is changed to avoid cyclic types in generated code */
            methodBody = methodBody + ".asInstanceOf[" +
                         field->getType()->str(locationManager) + "]";
        }
    }
    std::stringstream s;
    s << "      def " << getter << ": " << returnType << " = " << methodBody
      << "\n";
    return s.str();
}

bool Struct::isRepresentedAsStruct() const {
    return fields.size() <= SCALA_NATIVE_MAX_STRUCT_FIELDS && !hasBitField;
}

std::string Struct::generateSetterForArrayRepresentation(
    unsigned int fieldIndex, const LocationManager &locationManager) const {
    std::shared_ptr<Field> field = fields[fieldIndex];
    std::string setter = handleReservedWords(field->getName(), "_=");
    std::string parameterType =
        wrapArrayOrRecordInPointer(field->getType())->str(locationManager);
    std::string value = "value";
    std::string castedField = "p._1";

    PointerType pointerToFieldType = PointerType(field->getType());
    uint64_t offsetInBytes = field->getOffsetInBits() / 8;
    if (offsetInBytes > 0) {
        castedField =
            "(" + castedField + " + " + std::to_string(offsetInBytes) + ")";
    }
    castedField = "!" + castedField + ".asInstanceOf[" +
                  pointerToFieldType.str(locationManager) + "]";
    std::vector<std::shared_ptr<const Struct>> structTypesThatShouldBeReplaced =
        shouldFieldBreakCycle(field);
    if (!structTypesThatShouldBeReplaced.empty()) {
        /* field type is changed to avoid cyclic types in generated code */
        std::shared_ptr<const Type> typeReplacement = getTypeReplacement(
            field->getType(), structTypesThatShouldBeReplaced);
        value = value + ".asInstanceOf[" + typeReplacement->str(locationManager) + "]";
    } else if (isArrayOrRecord(field->getType())) {
        //value = "!" + value;
    }
    std::stringstream s;
    s << "      def " << setter
      << "(value: " + parameterType + "): Unit = " << castedField << " = "
      << value << "\n";
    return s.str();
}

std::string Struct::generateGetterForArrayRepresentation(
    unsigned fieldIndex, const LocationManager &locationManager) const {
    std::shared_ptr<Field> field = fields[fieldIndex];
    std::string getter = handleReservedWords(field->getName());
    std::string returnType =
        wrapArrayOrRecordInPointer(field->getType())->str(locationManager);
    std::string methodBody;

    PointerType pointerToFieldType = PointerType(field->getType());
    uint64_t offsetInBytes = field->getOffsetInBits() / 8;
    if (offsetInBytes > 0) {
        methodBody = "(p._1 + " + std::to_string(offsetInBytes) + ")";
    } else {
        methodBody = "p._1";
    }
    methodBody =
        methodBody + ".asInstanceOf[" + pointerToFieldType.str(locationManager) + "]";

    if (!isArrayOrRecord(field->getType())) {
        //methodBody = "!" + methodBody;
        if (!shouldFieldBreakCycle(field).empty()) {
            /* field type is changed to avoid cyclic types in generated code */
            methodBody = "(" + methodBody + ").asInstanceOf[" +
                         field->getType()->str(locationManager) + "]";
        }
    }
    std::stringstream s;
    s << "      def " << getter << ": " << returnType << " = " << methodBody
      << "\n";
    return s.str();
}

std::shared_ptr<const Type>
Struct::getTypeReplacement(std::shared_ptr<const Type> type,
                           std::vector<std::shared_ptr<const Struct>>
                               structTypesThatShouldBeReplaced) const {
    std::shared_ptr<const Type> replacementType = type->unrollTypedefs();
    std::shared_ptr<PointerType> pointerToByte =
        std::make_shared<PointerType>(std::make_shared<PrimitiveType>("Byte"));
    for (const auto &recordType : structTypesThatShouldBeReplaced) {
        std::shared_ptr<TypeDef> recordTypeDef = std::make_shared<TypeDef>(
            recordType->getTypeName(), recordType, nullptr);
        std::shared_ptr<Type> pointerToRecord =
            std::make_shared<PointerType>(recordTypeDef);
        if (*replacementType == *pointerToRecord) {
            replacementType = pointerToByte;
        } else {
            replacementType =
                replacementType->replaceType(pointerToRecord, pointerToByte);
        }
        std::vector<std::shared_ptr<const Type>> visitedTypes;
        if (replacementType->usesType(recordType, false, visitedTypes)) {
            assert(isInstanceOf<FunctionPointerType>(replacementType.get()));
            /* function pointer types may have return value or a parameter of
             * value type */
            replacementType = replacementType->replaceType(
                recordTypeDef,
                std::make_shared<PrimitiveType>("CStruct0"));
        }
    }
    return replacementType;
}

std::vector<std::shared_ptr<const Struct>>
Struct::shouldFieldBreakCycle(const std::shared_ptr<Field> &field) const {
    std::vector<std::shared_ptr<const Struct>> structTypesThatShouldBeReplaced;
    if (isAliasForType<Struct>(field->getType().get()) ||
        isAliasForType<Union>(field->getType().get())) {
        /* cycle should be broken on pointer type */
        return structTypesThatShouldBeReplaced;
    }
    CycleNode baseNode(shared_from_base<Struct>(), field.get());
    std::vector<std::shared_ptr<const Type>> visitedTypes;
    if (field->getType()->findAllCycles(shared_from_base<Struct>(), baseNode,
                                        visitedTypes)) {
        /* one or more cycles were found but type of the filed should be
         * changed if this struct has the biggest name compared to other structs
         * in cycle that have fields of non-value type.
         */
        if (baseNode.cycleNodes.empty()) {
            /* field references containing struct */
            structTypesThatShouldBeReplaced.push_back(
                shared_from_base<Struct>());
        }
        for (const auto &nextCycleNode : baseNode.cycleNodes) {
            std::vector<std::string> namesInCycle;
            if (hasBiggestName(nextCycleNode, namesInCycle)) {
                structTypesThatShouldBeReplaced.push_back(nextCycleNode.s);
            }
        }
    }
    return structTypesThatShouldBeReplaced;
}

bool Struct::findAllCycles(
    const std::shared_ptr<const Struct> &startStruct, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (this == startStruct.get()) {
        return true;
    }
    /* visitedTypes check is ignored because it is not necessary to save Struct
     * and Union types in visitedTypes if it is done for TypeDefs (Struct and
     * Union types can be references only through TypeDefs) */
    bool belongsToCycle = false;
    for (const auto &field : fields) {
        CycleNode newCycleNode(shared_from_base<Struct>(), field.get());
        if (field->getType()->findAllCycles(startStruct, newCycleNode,
                                            visitedTypes)) {
            if (isAliasForType<Struct>(field->getType().get()) ||
                isAliasForType<Union>(field->getType().get())) {
                /* cycles cannot be broken on value type fields */
                newCycleNode.isValueType = true;
            }
            belongsToCycle = true;
            cycleNode.cycleNodes.push_back(newCycleNode);
        }
    }
    return belongsToCycle;
}

bool Struct::hasBiggestName(const CycleNode &node,
                            std::vector<std::string> namesInCycle) const {
    if (!node.isValueType) {
        namesInCycle.push_back(node.s->getTypeName());
    }
    if (node.cycleNodes.empty()) {
        std::sort(namesInCycle.begin(), namesInCycle.end());
        return getTypeName() >= namesInCycle.back();
    }
    for (const auto &cycleNode : node.cycleNodes) {
        if (hasBiggestName(cycleNode, namesInCycle)) {
            return true;
        }
    }
    return false;
}

std::string
Struct::getConstructorHelper(const LocationManager &locationManager) const {
    std::stringstream s;
    std::string type = replaceChar(getTypeName(), " ", "_");
    s << "  object " << type << " {\n"
      << "    import implicits._\n";

    /* constructor with no parameters */
    s << "    def apply()(implicit z: Zone): Ptr[" + type + "]"
      << " = alloc[" + type + "]\n";

    /* constructor that initializes all fields */
    s << "    def apply(";
    std::string sep = "";
    for (const auto &field : fields) {
        s << sep << handleReservedWords(field->getName()) << ": "
          << wrapArrayOrRecordInPointer(field->getType())->str(locationManager);
        sep = ", ";
    }
    s << ")(implicit z: Zone): Ptr[" << type << "] = {\n"
      << "      val ptr = alloc[" << type << "]\n";
    for (const auto &field : fields) {
        std::string name = handleReservedWords(field->getName());
        s << "      ptr." << name << " = " << name << "\n";
    }
    s << "      ptr\n"
      << "    }\n"
      << "  }\n";
    return s.str();
}

bool Struct::isArrayOrRecord(std::shared_ptr<const Type> type) const {
    return isAliasForType<ArrayType>(type.get()) ||
           isAliasForType<Struct>(type.get());
}

std::shared_ptr<const Type>
Struct::wrapArrayOrRecordInPointer(std::shared_ptr<const Type> type) const {
    if (isArrayOrRecord(type)) {
        return std::make_shared<PointerType>(type);
    }
    return type;
}
