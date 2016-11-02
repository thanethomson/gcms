package com.thanethomson.gcms.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.thanethomson.gcms.enums.FieldType
import java.text.SimpleDateFormat
import java.util.*


fun parseTypeSpecFields(json: JsonNode): Map<String, FieldSpec> {
    val fields = HashMap<String, FieldSpec>()
    for (fieldName in json.fieldNames()) {
        fields[fieldName] = FieldSpec(json.get(fieldName))
    }
    return fields
}

fun parseFieldSpecType(json: JsonNode): FieldType
        = FieldType.fromId(if (json.isTextual) json.asText() else json.get("type").asText())

fun parseFieldSpecDefault(json: JsonNode): Any?
        = if (json.isTextual) null else (if (json.has("default")) extractDefaultFromJsonFieldSpec(json) else null)

fun parseFieldSpecMinSize(json: JsonNode): Int?
        = if (json.isTextual) null else (if (json.has("minSize")) json.get("minSize").asInt() else null)

fun parseFieldSpecMaxSize(json: JsonNode): Int?
        = if (json.isTextual) null else (if (json.has("maxSize")) json.get("maxSize").asInt() else null)

fun parseFieldSpecReferences(json: JsonNode): String?
        = if (json.isTextual) null else (if (json.has("references")) json.get("references").asText() else null)

fun extractDefaultFromJsonFieldSpec(json: JsonNode): Any? {
    val type = parseFieldSpecType(json)
    when (type) {
        FieldType.STRING -> return json.get("default").asText()
        FieldType.INT -> return json.get("default").asInt()
        FieldType.BIGINT -> return json.get("default").asLong()
        FieldType.TEXT -> return json.get("default").asText()
        FieldType.FLOAT -> return json.get("default").asDouble()
        FieldType.BOOLEAN -> return json.get("default").asBoolean()
        FieldType.DATETIME -> return parseJsonDateTime(json.get("default").asText())
        FieldType.DATE -> return parseJsonDate(json.get("default").asText())
        FieldType.FOREIGN_KEY -> return json.get("default").asText()
    }
}

fun putJsonForFieldSpecDefault(parent: ObjectNode, type: FieldType, default: Any?) {
    when (type) {
        FieldType.STRING -> parent.put("default", default as String)
        FieldType.INT -> parent.put("default", default as Int)
        FieldType.BIGINT -> parent.put("default", default as Long)
        FieldType.FLOAT -> parent.put("default", default as Double)
        FieldType.TEXT -> parent.put("default", default as String)
        FieldType.BOOLEAN -> parent.put("default", default as Boolean)
        FieldType.DATETIME -> parent.put("default", ISO8601DateFormat().format(default as Date))
        FieldType.DATE -> parent.put("default", SimpleDateFormat("yyyy-MM-dd").format(default as Date))
        FieldType.FOREIGN_KEY -> parent.put("default", default as String)
    }
}

/**
 * We assume ISO8601 for date/time formatting.
 */
fun parseJsonDateTime(s: String): Date = ISO8601DateFormat().parse(s)

/**
 * Only parse simple dates in yyyy-MM-dd format.
 */
fun parseJsonDate(s: String): Date = SimpleDateFormat("yyyy-MM-dd").parse(s)
