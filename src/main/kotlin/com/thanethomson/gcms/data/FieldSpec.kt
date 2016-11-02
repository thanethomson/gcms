package com.thanethomson.gcms.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.thanethomson.gcms.enums.FieldType

data class FieldSpec(
    /**
     * What type of field is this?
     */
    val type: FieldType,

    /**
     * The default value for this field, if any.
     */
    val default: Any? = null,

    /**
     * The minimum size for this field, if any.
     */
    val minSize: Int? = null,

    /**
     * The maximum size for this field, if any.
     */
    val maxSize: Int? = null,

    /**
     * If this is a foreign key field, which field does it reference?
     */
    val references: String? = null
) {

    constructor(json: JsonNode): this(
        parseFieldSpecType(json),
        parseFieldSpecDefault(json),
        parseFieldSpecMinSize(json),
        parseFieldSpecMaxSize(json),
        parseFieldSpecReferences(json)
    )

    fun toJson(): JsonNode {
        val json: JsonNode
        if (isSimple()) {
            json = JsonNodeFactory.instance.textNode(type.id)
        } else {
            json = JsonNodeFactory.instance.objectNode()
            json.put("type", type.id)
            if (default != null) {
                putJsonForFieldSpecDefault(json as ObjectNode, type, default)
            }
            if (minSize != null) {
                json.put("minSize", minSize)
            }
            if (maxSize != null) {
                json.put("maxSize", maxSize)
            }
            if (references != null) {
                json.put("references", references)
            }
        }
        return json
    }

    fun toJsonString(): String = toJson().toString()

    fun isSimple(): Boolean = (default == null && minSize == null && maxSize == null && references == null)

}
