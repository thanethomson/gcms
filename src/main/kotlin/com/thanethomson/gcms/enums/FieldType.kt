package com.thanethomson.gcms.enums

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.thanethomson.gcms.data.parseJsonDate
import com.thanethomson.gcms.data.parseJsonDateTime
import com.thanethomson.gcms.data.serializeJsonDate
import com.thanethomson.gcms.data.serializeJsonDateTime
import com.thanethomson.gcms.errors.JsonBuildError
import java.util.*


enum class FieldType constructor(val id: String) {
    STRING("string"),
    INT("int"),
    BIGINT("bigint"),
    TEXT("text"),
    FLOAT("float"),
    BOOLEAN("bool"),
    DATETIME("datetime"),
    DATE("date"),
    FOREIGN_KEY("fk");

    companion object {
        @JvmStatic val FIELD_TYPES_BY_ID = HashMap<String, FieldType>()
        @JvmStatic val JSON_FIELD_VALUE_PARSERS = HashMap<FieldType, (JsonNode) -> Any?>()
        @JvmStatic val JSON_FIELD_VALUE_SERIALIZERS = HashMap<FieldType, (ObjectNode, String, Any?) -> Unit>()
        @JvmStatic val STRING_TYPES = setOf(STRING, TEXT, FOREIGN_KEY)

        init {
            for (fieldType in FieldType.values()) {
                FIELD_TYPES_BY_ID[fieldType.id] = fieldType
            }
            JSON_FIELD_VALUE_PARSERS.put(STRING, JsonNode::asText)
            JSON_FIELD_VALUE_PARSERS.put(INT, JsonNode::asInt)
            JSON_FIELD_VALUE_PARSERS.put(BIGINT, JsonNode::asLong)
            JSON_FIELD_VALUE_PARSERS.put(TEXT, JsonNode::asText)
            JSON_FIELD_VALUE_PARSERS.put(FLOAT, JsonNode::asDouble)
            JSON_FIELD_VALUE_PARSERS.put(BOOLEAN, JsonNode::asBoolean)
            JSON_FIELD_VALUE_PARSERS.put(DATETIME, { json -> parseJsonDateTime(json.asText()) })
            JSON_FIELD_VALUE_PARSERS.put(DATE, { json -> parseJsonDate(json.asText()) })
            JSON_FIELD_VALUE_PARSERS.put(FOREIGN_KEY, JsonNode::asText)

            JSON_FIELD_VALUE_SERIALIZERS.put(STRING, { parent, field, v -> parent.put(field, v as String) })
            JSON_FIELD_VALUE_SERIALIZERS.put(INT, { parent, field, v -> parent.put(field, v as Int) })
            JSON_FIELD_VALUE_SERIALIZERS.put(BIGINT, { parent, field, v -> parent.put(field, v as Long) })
            JSON_FIELD_VALUE_SERIALIZERS.put(TEXT, { parent, field, v -> parent.put(field, v as String) })
            JSON_FIELD_VALUE_SERIALIZERS.put(FLOAT, { parent, field, v -> parent.put(field, v as Double) })
            JSON_FIELD_VALUE_SERIALIZERS.put(BOOLEAN, { parent, field, v -> parent.put(field, v as Boolean) })
            JSON_FIELD_VALUE_SERIALIZERS.put(DATETIME, { parent, field, v -> parent.put(field, serializeJsonDateTime(v as Date)) })
            JSON_FIELD_VALUE_SERIALIZERS.put(DATE, { parent, field, v -> parent.put(field, serializeJsonDate(v as Date)) })
            JSON_FIELD_VALUE_SERIALIZERS.put(FOREIGN_KEY, { parent, field, v ->
                if (v is String)
                    parent.put(field, v)
                else
                    throw JsonBuildError("Unrecognised field value for field \"$field\"")
            })
        }

        @JvmStatic fun fromId(id: String): FieldType
            = FIELD_TYPES_BY_ID[id] ?: throw IllegalArgumentException("Unknown field type: $id")

    }

    fun parseJsonValue(json: JsonNode): Any?
        = JSON_FIELD_VALUE_PARSERS[this]?.invoke(json) ?: null

    fun serializeJsonValue(parent: JsonNode, fieldName: String, v: Any?) {
        if (v == null) {
            (parent as ObjectNode).putNull(fieldName)
        } else {
            JSON_FIELD_VALUE_SERIALIZERS[this]?.invoke(parent as ObjectNode, fieldName, v) ?:
                (parent as ObjectNode).putNull(fieldName)
        }
    }

    fun isTextual(): Boolean
        = STRING_TYPES.contains(this)

}
