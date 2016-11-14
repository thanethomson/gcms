package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.storage.StorageQuery
import com.thanethomson.gcms.data.storage.TypeInstance
import com.thanethomson.gcms.data.storage.TypeSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

/**
 * Storage engine for PostgreSQL.
 *
 * @param url: The JDBC connection string for connecting to PostgreSQL.
 * @param username: The username for the PostgreSQL user.
 * @param password: The password for the PostgreSQL user.
 */
class PostgresStorageEngine constructor(
        val url: String,
        val username: String,
        val password: String
): StorageEngine {

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(PostgresStorageEngine::class.java)
    }

    val conn: Connection

    init {
        // verify that the PostgreSQL driver is available
        Class.forName("org.postgresql.Driver")

        logger.info("Attempting to connect to PostgreSQL server as $username: $url")
        // connect to PostgreSQL
        conn = DriverManager.getConnection(url, username, password)
        conn.autoCommit = false
    }

    override fun applyMigrations() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun flush() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shutdown() {
        if (!conn.isClosed) {
            logger.info("Shutting down PostgreSQL connection")
            conn.close()
        }
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

}