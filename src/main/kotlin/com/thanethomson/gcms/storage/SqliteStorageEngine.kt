package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.storage.StorageQuery
import com.thanethomson.gcms.data.storage.TypeInstance
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.enums.FieldType
import com.thanethomson.gcms.errors.TypeSpecError
import com.thanethomson.gcms.utils.StringValidation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

/**
 * Storage engine for SQLite. Allows for in-memory databases, which helps with unit testing.
 */
class SqliteStorageEngine constructor(
    val url: String
): AbstractSqlStorageEngine("sqlite") {

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(SqliteStorageEngine::class.java)
        @JvmStatic val SQL_TYPE_MAP = HashMap<FieldType, String>()

        init {
            SQL_TYPE_MAP.put(FieldType.STRING, "VARCHAR")
            SQL_TYPE_MAP.put(FieldType.INT, "INT")
            SQL_TYPE_MAP.put(FieldType.BIGINT, "BIGINT")
            SQL_TYPE_MAP.put(FieldType.TEXT, "TEXT")
            SQL_TYPE_MAP.put(FieldType.FLOAT, "DOUBLE")
            SQL_TYPE_MAP.put(FieldType.BOOLEAN, "BOOLEAN")
            SQL_TYPE_MAP.put(FieldType.DATETIME, "BIGINT")
            SQL_TYPE_MAP.put(FieldType.DATE, "BIGINT")
            SQL_TYPE_MAP.put(FieldType.FOREIGN_KEY, "VARCHAR(100)")
        }
    }

    val conn: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        logger.info("Connecting to SQLite database: $url")
        conn = DriverManager.getConnection("jdbc:$url")
        conn.autoCommit = false
        checkMetaTables(conn)
    }

    override fun applyMigrations() {
        logger.debug("Scanning database migrations...")
        val existing = extractMigrationInfo(
            conn,
            "SELECT type_name, type_spec, drop_table FROM migrations WHERE applied IS NOT NULL ORDER BY applied DESC"
        )
        logger.debug("Loaded ${existing.a.size} existing type(s)")
        val desired = extractMigrationInfo(
            conn,
            "SELECT type_name, type_spec, drop_table FROM migrations WHERE applied IS NULL ORDER BY created DESC"
        )
        logger.debug("Loaded ${desired.a.size} new type(s) and ${desired.b.size} deleted type(s)")

        // first check for any dropped types
        for (typeName in desired.b) {
            executeUpdate(conn, "DROP TABLE $typeName")
            logger.info("Dropped table \"$typeName\"")
        }

        // now check for any modified or new types
        for ((typeName, typeSpec) in desired.a) {
            if (existing.a.containsKey(typeName)) {
                // the type was modified
                if (existing.a != desired.a) {

                }
            } else {
                // if it's a new type

            }
        }
    }

    override fun typeSpecToSql(name: String, spec: TypeSpec): String {
        // just to be sure
        if (!StringValidation.isValidSqlId(name)) {
            throw TypeSpecError("Illegal characters in type name: $name")
        }

        var sql = "CREATE TABLE $name (\n  id VARCHAR(100) UNIQUE PRIMARY KEY"

        for ((fieldName, fieldSpec) in spec.fields) {
            sql += ",\n  $fieldName ${SQL_TYPE_MAP[fieldSpec.type]}"

            if (fieldSpec.type == FieldType.STRING) {
                sql += "(${fieldSpec.maxSize ?: 500})"
            }
            if (fieldSpec.default != null) {
                sql += " DEFAULT "
                if (fieldSpec.default is String && fieldSpec.type.isTextual()) {
                    sql += "'" + fieldSpec.default.replace(Regex("([^\\\\])\'"), "\$1\\'") + "'"
                } else {
                    sql += "${fieldSpec.default}"
                }
            }
        }
        sql += "\n)"

        return sql
    }

    override fun getTypes(): Map<String, TypeSpec> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun upsertType(name: String, spec: TypeSpec) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(name: String): TypeSpec {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteType(name: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createInstance(type: String, fieldValues: Map<String, Any?>): TypeInstance {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateInstance(type: String, id: String, fieldValues: Map<String, Any?>): TypeInstance {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstance(type: String, id: String, relatedDepth: Int): TypeInstance {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllInstances(type: String, orderBy: String?, orderAsc: Boolean, limit: Int?, relatedDepth: Int?): List<TypeInstance> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findInstances(type: String, query: StorageQuery, orderBy: String?, orderAsc: Boolean, limit: Int?, relatedDepth: Int?): List<TypeInstance> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteInstance(type: String, id: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun flush() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shutdown() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}