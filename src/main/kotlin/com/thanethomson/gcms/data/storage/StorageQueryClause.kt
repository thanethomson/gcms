package com.thanethomson.gcms.data.storage

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.collect.Lists
import com.thanethomson.gcms.enums.QueryOp
import com.thanethomson.gcms.errors.QueryParseError

/**
 * A simple storage query clause: <field> <op> <value>
 *
 * e.g. someField == 123
 */
data class StorageQueryClause(
    val field: String,
    val op: QueryOp,
    val value: Any?
) {

    companion object {
        @JvmStatic fun fromJson(op: QueryOp, json: JsonNode, level: Int = 0): StorageQueryClause {
            if (!json.isObject || json.size() == 0) {
                throw QueryParseError("Query clause must be a non-empty JSON object (at level $level)")
            }
            if (json.size() > 1) {
                throw QueryParseError("Query clause can only have a single field/value (at level $level)")
            }
            val field = Lists.newArrayList<String>(json.fieldNames())[0]
            return StorageQueryClause(field, op, parseFieldValue(field, json.get(0), level))
        }

        @JvmStatic fun parseFieldValue(field: String, node: JsonNode, level: Int = 0): Any? {
            if (node.isTextual) {
                return node.asText()
            } else if (node.isDouble || node.isFloat) {
                return node.asDouble()
            } else if (node.isInt) {
                return node.asInt()
            } else if (node.isLong) {
                return node.asLong()
            } else if (node.isBoolean) {
                return node.asBoolean()
            } else if (node.isNull) {
                return null
            } else {
                throw QueryParseError("Unrecognised or invalid field type for \"$field\" (at level $level)")
            }
        }
    }

}
