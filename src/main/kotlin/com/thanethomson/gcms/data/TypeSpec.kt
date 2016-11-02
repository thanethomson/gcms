package com.thanethomson.gcms.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Allows us to describe a data type.
 */
data class TypeSpec(
    /**
     * A mapping of field names to field types.
     */
    val fields: Map<String, FieldSpec>
) {

    /**
     * Constructor to build the type spec from a JSON object.
     */
    constructor(json: JsonNode): this(
        parseTypeSpecFields(json)
    )

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
