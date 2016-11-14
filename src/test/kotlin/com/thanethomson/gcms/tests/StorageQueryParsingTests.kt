package com.thanethomson.gcms.tests

import com.thanethomson.gcms.Application
import com.thanethomson.gcms.data.storage.StorageQuery
import com.thanethomson.gcms.data.storage.StorageQueryClause
import com.thanethomson.gcms.enums.ClauseOp
import com.thanethomson.gcms.enums.QueryOp
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner::class)
@ActiveProfiles("test")
@SpringBootTest(classes = arrayOf(Application::class))
class StorageQueryParsingTests {

    companion object {
        @JvmStatic val BASIC_VALID_QUERY1 = "{\"id\": \"something\"}"
        @JvmStatic val BASIC_VALID_QUERY2 = "{\"id\": \"something\", \"param1\": 12345}"
        @JvmStatic val BASIC_VALID_QUERY3 = "{\"param1\": 12345, \"param2\": true, \"param3\": 32.5}"

        @JvmStatic val BASIC_VALID_COMP_QUERY1 = "{\"\$neq\": { \"id\": \"something\", \"param1\": 12345 } }"
        @JvmStatic val BASIC_VALID_COMP_QUERY2 = "{\"\$gt\": { \"param1\": 12345, \"param2\": 54321 } }"

        @JvmStatic val BASIC_VALID_OP_QUERY1 = "{\"\$or\": { \"id\": \"something\", \"param1\": 12345 } }"
        @JvmStatic val BASIC_VALID_OP_QUERY2 = "{\"\$not\": {\"id\": \"something\" } }"
        @JvmStatic val BASIC_VALID_OP_QUERY3 = "{\"\$and\": {\"id\": \"something\", \"param1\": 12345 } }"

        @JvmStatic val COMPLEX_VALID_QUERY1 =
            "{"+
            "    \"\$or\": {" +
            "        \"param4\": true,"+
            "        \"\$gt\": { \"param1\": 12345 },"+
            "        \"\$lt\": { \"param2\": 54321 }"+
            "    },"+
            "    \"\$not\": {"+
            "        \"\$like\": { \"param3\": \"SOMETHING%\" }"+
            "    }," +
            "    \"param5\": 3.14159,"+
            "    \"\$and\": {"+
            "        \"\$not\": {"+
            "            \"\$neq\": { \"param3\": \"SOMETHING\" }"+
            "        },"+
            "        \"\$gte\": { \"param1\": 12345 },"+
            "        \"\$lte\": { \"param2\": 54321 }"+
            "    }"+
            "}"
    }

    @Test
    fun testParseBasicQueries() {
        var query = StorageQuery.fromJson(BASIC_VALID_QUERY1)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                listOf(StorageQueryClause("id", QueryOp.EQ, "something")),
                emptyList()
            ),
            query
        )

        query = StorageQuery.fromJson(BASIC_VALID_QUERY2)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                listOf(
                    StorageQueryClause("id", QueryOp.EQ, "something"),
                    StorageQueryClause("param1", QueryOp.EQ, 12345)
                ),
                emptyList()
            ),
            query
        )

        query = StorageQuery.fromJson(BASIC_VALID_QUERY3)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                listOf(
                    StorageQueryClause("param1", QueryOp.EQ, 12345),
                    StorageQueryClause("param2", QueryOp.EQ, true),
                    StorageQueryClause("param3", QueryOp.EQ, 32.5)
                ),
                emptyList()
            ),
            query
        )
    }

    @Test
    fun testParseBasicComparisonQueries() {
        var query = StorageQuery.fromJson(BASIC_VALID_COMP_QUERY1)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                emptyList(),
                listOf(
                    StorageQuery(
                        ClauseOp.AND,
                        listOf(
                            StorageQueryClause("id", QueryOp.NEQ, "something"),
                            StorageQueryClause("param1", QueryOp.NEQ, 12345)
                        ),
                        emptyList()
                    )
                )
            ),
            query
        )

        query = StorageQuery.fromJson(BASIC_VALID_COMP_QUERY2)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                emptyList(),
                listOf(
                    StorageQuery(
                        ClauseOp.AND,
                        listOf(
                            StorageQueryClause("param1", QueryOp.GT, 12345),
                            StorageQueryClause("param2", QueryOp.GT, 54321)
                        ),
                        emptyList()
                    )
                )
            ),
            query
        )
    }

    @Test
    fun testParseBasicOpQueries() {
        var query = StorageQuery.fromJson(BASIC_VALID_OP_QUERY1)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                emptyList(),
                listOf(
                    StorageQuery(
                        ClauseOp.OR,
                        listOf(
                            StorageQueryClause("id", QueryOp.EQ, "something"),
                            StorageQueryClause("param1", QueryOp.EQ, 12345)
                        ),
                        emptyList()
                    )
                )
            ),
            query
        )

        query = StorageQuery.fromJson(BASIC_VALID_OP_QUERY2)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                emptyList(),
                listOf(
                    StorageQuery(
                        ClauseOp.NOT,
                        listOf(StorageQueryClause("id", QueryOp.EQ, "something")),
                        emptyList()
                    )
                )
            ),
            query
        )

        query = StorageQuery.fromJson(BASIC_VALID_OP_QUERY3)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                emptyList(),
                listOf(
                    StorageQuery(
                        ClauseOp.AND,
                        listOf(
                            StorageQueryClause("id", QueryOp.EQ, "something"),
                            StorageQueryClause("param1", QueryOp.EQ, 12345)
                        ),
                        emptyList()
                    )
                )
            ),
            query
        )
    }

    @Test
    fun testParseComplexQuery() {
        val query = StorageQuery.fromJson(COMPLEX_VALID_QUERY1)
        assertEquals(
            StorageQuery(
                ClauseOp.AND,
                listOf(StorageQueryClause("param5", QueryOp.EQ, 3.14159)),
                listOf(
                    StorageQuery(
                        ClauseOp.OR,
                        listOf(
                            StorageQueryClause("param4", QueryOp.EQ, true)
                        ),
                        listOf(
                            StorageQuery(
                                ClauseOp.AND,
                                listOf(StorageQueryClause("param1", QueryOp.GT, 12345)),
                                emptyList()
                            ),
                            StorageQuery(
                                ClauseOp.AND,
                                listOf(StorageQueryClause("param2", QueryOp.LT, 54321)),
                                emptyList()
                            )
                        )
                    ),
                    StorageQuery(
                        ClauseOp.NOT,
                        emptyList(),
                        listOf(
                            StorageQuery(
                                ClauseOp.AND,
                                listOf(StorageQueryClause("param3", QueryOp.LIKE, "SOMETHING%")),
                                emptyList()
                            )
                        )
                    ),
                    StorageQuery(
                        ClauseOp.AND,
                        emptyList(),
                        listOf(
                            StorageQuery(
                                ClauseOp.NOT,
                                emptyList(),
                                listOf(
                                    StorageQuery(
                                        ClauseOp.AND,
                                        listOf(StorageQueryClause("param3", QueryOp.NEQ, "SOMETHING")),
                                        emptyList()
                                    )
                                )
                            ),
                            StorageQuery(
                                ClauseOp.AND,
                                listOf(StorageQueryClause("param1", QueryOp.GTE, 12345)),
                                emptyList()
                            ),
                            StorageQuery(
                                ClauseOp.AND,
                                listOf(StorageQueryClause("param2", QueryOp.LTE, 54321)),
                                emptyList()
                            )
                        )
                    )
                )
            ),
            query
        )
    }

}
