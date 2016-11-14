package com.thanethomson.gcms.enums

import java.util.*

enum class QueryOp(val id: String) {
    EQ("\$eq"),
    NEQ("\$neq"),
    GT("\$gt"),
    GTE("\$gte"),
    LT("\$lt"),
    LTE("\$lte"),
    LIKE("\$like"),
    ILIKE("\$ilike");

    companion object {
        @JvmStatic val queryOpsById = HashMap<String, QueryOp>()

        init {
            for (op in QueryOp.values()) {
                queryOpsById[op.id] = op
            }
        }

        @JvmStatic fun getById(id: String): QueryOp
            = queryOpsById[id] ?: throw IllegalArgumentException("Unrecognised query operation: $id")

    }
}
