package com.thanethomson.gcms.data.storage

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.thanethomson.gcms.data.parseJsonString
import com.thanethomson.gcms.errors.JsonParseError
import com.thanethomson.gcms.errors.TypeSpecError
import com.thanethomson.gcms.utils.StringValidation
import java.util.*

/**
 * Allows us to describe a data type.
 */
data class TypeSpec(
    /**
     * A mapping of field names to field types.
     */
    val fields: Map<String, FieldSpec>
) {

    companion object {
        @JvmStatic fun fromJson(json: JsonNode): TypeSpec {
            if (!json.isObject || json.size() == 0) {
                throw JsonParseError("Type spec must be a non-empty JSON object")
            }
            val fields = HashMap<String, FieldSpec>()
            for ((fieldName, node) in json.fields()) {
                fields.put(fieldName, FieldSpec.fromJson(node))
            }
            return TypeSpec(fields)
        }

        @JvmStatic fun fromJson(json: String): TypeSpec
            = fromJson(parseJsonString(json))
    }

    init {
        for ((fieldName, fieldSpec) in fields) {
            if (!StringValidation.isValidSqlId(fieldName)) {
                throw TypeSpecError("Illegal characters in field name: $fieldName")
            }
        }
    }

    fun toJson(): JsonNode {
        val json = JsonNodeFactory.instance.objectNode()
        var fieldJson: JsonNode
        for ((fieldName, field) in fields) {
            fieldJson = field.toJson()

            // it's either text or an object
            if (fieldJson.isTextual) {
                json.put(fieldName, fieldJson.asText())
            } else {
                json.putObject(fieldName).setAll(fieldJson as ObjectNode)
            }
        }
        return json
    }

    fun toJsonString(): String = toJson().toString()

}
