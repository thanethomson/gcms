package com.thanethomson.gcms.data.storage

import com.fasterxml.jackson.databind.JsonNode
import com.thanethomson.gcms.data.parseJsonString
import com.thanethomson.gcms.enums.ClauseOp
import com.thanethomson.gcms.enums.QueryOp
import com.thanethomson.gcms.errors.QueryParseError
import java.util.*

data class StorageQuery(

    /**
     * The operator to use to join the sub-queries/clauses.
     */
    val op: ClauseOp,

    /**
     * The direct clauses to join if this is a simple query.
     */
    val clauses: List<StorageQueryClause>,

    /**
     * The sub-queries to join, if this is a complex query.
     */
    val subQueries: List<StorageQuery>

) {

    companion object {
        /**
         * Recursively parses a query from the given JSON.
         */
        @JvmStatic fun fromJson(
            json: JsonNode,
            clauseOp: ClauseOp = ClauseOp.AND,
            queryOp: QueryOp = QueryOp.EQ,
            level: Int = 0
        ): StorageQuery {
            if (!json.isObject || json.size() == 0) {
                throw QueryParseError("Query must be a non-empty JSON object (at level $level)")
            }

            val clauses = ArrayList<StorageQueryClause>()
            val subQueries = ArrayList<StorageQuery>()

            for ((fieldName, node) in json.fields()) {
                // check if it's a clause operator, assume it's a sub-query
                if (ClauseOp.clauseOpsById.containsKey(fieldName)) {
                    // recursively parse the sub-query
                    subQueries.add(
                        fromJson(node, ClauseOp.getById(fieldName), queryOp, level+1)
                    )
                } else if (QueryOp.queryOpsById.containsKey(fieldName)) {
                    // parse the sub-query
                    subQueries.add(
                        fromJson(node, ClauseOp.AND, QueryOp.getById(fieldName), level+1)
                    )
                } else {
                    // we have to assume it's a clause of some kind
                    if (QueryOp.queryOpsById.containsKey(fieldName)) {
                        clauses.add(
                            StorageQueryClause.fromJson(
                                QueryOp.getById(fieldName),
                                node,
                                level
                            )
                        )
                    } else {
                        // by default we assume it's a root-level clause
                        clauses.add(
                            StorageQueryClause(
                                fieldName,
                                queryOp,
                                StorageQueryClause.parseFieldValue(fieldName, node, level)
                            )
                        )
                    }
                }
            }

            // NOT logic validation
            if (clauseOp == ClauseOp.NOT && (clauses.size + subQueries.size) > 1) {
                throw QueryParseError("Only one sub-clause or query is allowed in a NOT operation (level $level)")
            }

            // clause operator defaults to AND
            return StorageQuery(clauseOp, clauses, subQueries)
        }

        @JvmStatic fun fromJson(json: String): StorageQuery
            = fromJson(parseJsonString(json))
    }

}
