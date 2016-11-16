package com.thanethomson.gcms.data

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.errors.JsonParseError
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun makeJsonStringFromTypeList(types: Map<String, TypeSpec>): String {
    val json = JsonNodeFactory.instance.objectNode()
    for ((typeName, spec) in types) {
        json.putObject(typeName).setAll(spec.toJson() as ObjectNode)
    }
    return json.toString()
}

fun parseJsonString(json: String): JsonNode {
    val result: JsonNode
    try {
        result = ObjectMapper().readTree(json)
    } catch (e: IOException) {
        throw JsonParseError(e.message ?: "Unable to parse JSON data", e)
    } catch (e: JsonProcessingException) {
        throw JsonParseError(e.message ?: "Unable to parse JSON data", e)
    }
    return result
}

/**
 * We assume ISO8601 for date/time formatting.
 */
fun parseJsonDateTime(s: String): Date = ISO8601DateFormat().parse(s)

fun serializeJsonDateTime(d: Date): String = ISO8601DateFormat().format(d)

/**
 * Only parse simple dates in yyyy-MM-dd format.
 */
fun parseJsonDate(s: String): Date = SimpleDateFormat("yyyy-MM-dd").parse(s)

fun serializeJsonDate(d: Date): String = SimpleDateFormat("yyyy-MM-dd").format(d)
