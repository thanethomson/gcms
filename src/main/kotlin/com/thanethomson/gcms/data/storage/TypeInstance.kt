package com.thanethomson.gcms.data.storage

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.thanethomson.gcms.data.*
import com.thanethomson.gcms.enums.FieldType
import com.thanethomson.gcms.errors.JsonBuildError
import com.thanethomson.gcms.errors.JsonParseError
import com.thanethomson.gcms.errors.TypeSpecError
import java.util.*

data class TypeInstance(

    /**
     * What's the name of the type of this instance?
     */
    val type: String,

    /**
     * The primary key value of this instance.
     */
    val id: String?,

    /**
     * The values for each of the fields.
     */
    val fieldValues: Map<String, Any?>

) {

    companion object {
        @JvmStatic fun fromJson(types: Map<String, TypeSpec>, type: String, json: JsonNode): TypeInstance {
            if (!json.isObject || json.size() == 0) {
                throw JsonParseError("Instance must be a non-empty JSON object")
            }
            val typeSpec = types[type] ?: throw TypeSpecError("Unknown type for instance: $type")
            var id: String? = null
            val fieldValues = HashMap<String, Any?>()

            if (json.hasNonNull("id")) {
                if (!json.get("id").isTextual) {
                    throw JsonParseError("Expecting string for \"id\" property")
                }
                id = json.get("id").asText()
            }

            // we're only interested in fields in the type spec
            for ((fieldName, fieldSpec) in typeSpec.fields) {
                var fieldValue: Any? = null
                if (json.hasNonNull(fieldName)) {
                    // if it's a related object and needs parsing
                    if (fieldSpec.type == FieldType.FOREIGN_KEY && json.get(fieldName).isObject && fieldSpec.references != null) {
                        // parse the sub-object
                        fieldValue = fromJson(types, fieldSpec.references, json.get(fieldName))
                    } else {
                        fieldValue = fieldSpec.type.parseJsonValue(json.get(fieldName))
                    }
                }
                fieldValues.put(fieldName, fieldValue)
            }
            return TypeInstance(type, id, fieldValues)
        }

        @JvmStatic fun fromJson(types: Map<String, TypeSpec>, type: String, json: String): TypeInstance
            = fromJson(types, type, parseJsonString(json))
    }

    /**
     * Converts this instance to a JSON object.
     *
     * @param types The full map of all of the type specs.
     * @return A JSON object.
     * @exception TypeSpecError If the type is not available in the given types map.
     * @exception JsonBuildError If a foreign key reference is of an unrecognisable type.
     */
    fun toJson(types: Map<String, TypeSpec>): JsonNode {
        if (type !in types) {
            throw TypeSpecError("Unknown type for instance: $type")
        }
        val typeSpec = types[type] ?: throw TypeSpecError("Missing type for instance: $type")
        val json = JsonNodeFactory.instance.objectNode()

        json.put("id", id)
        for ((fieldName, fieldValue) in fieldValues) {
            // we only consider fields that are in the type spec
            if (fieldName in typeSpec.fields) {
                if (fieldValue == null) {
                    json.putNull(fieldName)
                } else {
                    // if we're serializing a complex object with sub-objects
                    if (typeSpec.fields[fieldName]?.type == FieldType.FOREIGN_KEY && fieldValue is TypeInstance) {
                        json.putObject(fieldName).setAll(fieldValue.toJson(types) as ObjectNode)
                    } else {
                        typeSpec.fields[fieldName]?.type?.serializeJsonValue(json, fieldName, fieldValue) ?:
                            throw JsonBuildError("Missing field information for field \"$fieldName\"")
                    }
                }
            }
        }

        return json
    }

    fun toJsonString(types: Map<String, TypeSpec>): String = toJson(types).toString()

}
