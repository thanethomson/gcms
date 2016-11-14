package com.thanethomson.gcms.enums

import java.util.*

enum class ClauseOp(val id: String) {
    AND("\$and"),
    OR("\$or"),
    NOT("\$not");

    companion object {
        @JvmStatic val clauseOpsById = HashMap<String, ClauseOp>()

        init {
            for (op in ClauseOp.values()) {
                clauseOpsById[op.id] = op
            }
        }

        @JvmStatic fun getById(id: String): ClauseOp
            = clauseOpsById[id] ?: throw IllegalArgumentException("Unrecognised clause operation ID: $id")
    }
}
