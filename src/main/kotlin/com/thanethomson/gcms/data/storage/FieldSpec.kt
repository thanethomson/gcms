package com.thanethomson.gcms.data.storage

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.thanethomson.gcms.data.*
import com.thanethomson.gcms.enums.FieldType
import com.thanethomson.gcms.errors.JsonParseError
import java.text.SimpleDateFormat
import java.util.*


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
    val references: String? = null,

    /**
     * Should this field be indexed? Makes it much faster to search.
     */
    val indexed: Boolean = false

) {

    companion object {
        @JvmStatic fun fromJson(json: JsonNode): FieldSpec {
            if (!json.isObject || json.size() == 0) {
                throw JsonParseError("Field spec JSON must be a non-empty object")
            }
            val type: FieldType
            var default: Any? = null
            var minSize: Int? = null
            var maxSize: Int? = null
            var references: String? = null
            var indexed: Boolean = false

            if (json.hasNonNull("type") && json.get("type").isTextual) {
                type = FieldType.fromId(json.get("type").asText())
            } else {
                throw JsonParseError("Missing or incorrect type of compulsory field spec field: \"type\"")
            }
            if (json.hasNonNull("default")) {
                when (type) {
                    FieldType.STRING -> default = json.get("default").asText()
                    FieldType.INT -> default = json.get("default").asInt()
                    FieldType.BIGINT -> default = json.get("default").asLong()
                    FieldType.TEXT -> default = json.get("default").asText()
                    FieldType.FLOAT -> default = json.get("default").asDouble()
                    FieldType.BOOLEAN -> default = json.get("default").asBoolean()
                    FieldType.DATETIME -> default = parseJsonDateTime(json.get("default").asText())
                    FieldType.DATE -> default = parseJsonDate(json.get("default").asText())
                    FieldType.FOREIGN_KEY -> default = json.get("default").asText()
                }
            }
            if (json.hasNonNull("minSize")) {
                if (!json.get("minSize").isInt) {
                    throw JsonParseError("Field \"minSize\" must be an integer")
                }
                minSize = json.get("minSize").asInt()
            }
            if (json.hasNonNull("maxSize")) {
                if (!json.get("maxSize").isInt) {
                    throw JsonParseError("Field \"maxSize\" must be an integer")
                }
                maxSize = json.get("maxSize").asInt()
            }
            if (json.hasNonNull("references")) {
                if (!json.get("references").isTextual) {
                    throw JsonParseError("Field \"references\" must be textual")
                }
                references = json.get("references").asText()
            }
            if (json.hasNonNull("indexed")) {
                if (!json.get("indexed").isBoolean) {
                    throw JsonParseError("Field \"indexed\" must be a boolean value")
                }
                indexed = json.get("indexed").asBoolean()
            }
            return FieldSpec(type, default, minSize, maxSize, references, indexed)
        }

        @JvmStatic fun fromJson(json: String): FieldSpec
            = fromJson(parseJsonString(json))
    }

    fun toJson(): JsonNode {
        val json: JsonNode
        if (isSimple()) {
            json = JsonNodeFactory.instance.textNode(type.id)
        } else {
            json = JsonNodeFactory.instance.objectNode()
            json.put("type", type.id)
            if (default != null) {
                when (type) {
                    FieldType.STRING -> json.put("default", default as String)
                    FieldType.INT -> json.put("default", default as Int)
                    FieldType.BIGINT -> json.put("default", default as Long)
                    FieldType.FLOAT -> json.put("default", default as Double)
                    FieldType.TEXT -> json.put("default", default as String)
                    FieldType.BOOLEAN -> json.put("default", default as Boolean)
                    FieldType.DATETIME -> json.put("default", ISO8601DateFormat().format(default as Date))
                    FieldType.DATE -> json.put("default", SimpleDateFormat("yyyy-MM-dd").format(default as Date))
                    FieldType.FOREIGN_KEY -> json.put("default", default as String)
                }
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
            json.put("indexed", indexed)
        }
        return json
    }

    fun toJsonString(): String = toJson().toString()

    fun isSimple(): Boolean = (default == null && minSize == null && maxSize == null && references == null)

}
