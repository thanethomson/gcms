package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.errors.DatabaseError
import com.thanethomson.gcms.utils.Tuple
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.nio.file.Paths
import java.sql.Connection
import java.sql.SQLException
import java.util.*

/**
 * Some generic functionality and helper utilities for SQL-based storage engines.
 */
abstract class AbstractSqlStorageEngine constructor(
    val resourcePath: String
): AbstractStorageEngine() {

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(AbstractSqlStorageEngine::class.java)
    }

    /**
     * Must convert the given type spec into a SQL "CREATE TABLE" statement.
     * @param name The name of the type.
     * @param spec A description of the field structure of the type.
     * @return A string containing the SQL statement.
     */
    abstract fun typeSpecToSql(name: String, spec: TypeSpec): String

    /**
     * Checks that the metadata tables exist.
     * @param conn The connection to use for checking if the meta tables exist.
     */
    fun checkMetaTables(conn: Connection) {
        logger.info("Ensuring users table exists")
        loadAndExecute(conn, "users.sql")
        logger.info("Ensuring migrations table exists")
        loadAndExecute(conn, "migrations.sql")
    }

    /**
     * Helper function to extract a list of types and dropped types from a SQL query to the migrations table.
     * @param conn The database connection to use.
     * @param query The SQL query to execute (database-specific).
     * @return A tuple containing a map of type names to type specs, as well as a set of names of dropped types.
     */
    fun extractMigrationInfo(conn: Connection, query: String): Tuple<HashMap<String, TypeSpec>, HashSet<String>> {
        val types = HashMap<String, TypeSpec>()
        val dropped = HashSet<String>()

        q {
            val s = conn.createStatement()
            val r = s.executeQuery(query)
            while (r.next()) {
                val typeName = r.getString("type_name")
                val typeSpec = r.getString("type_spec")
                val dropType = r.getBoolean("drop_type")

                if (!types.containsKey(typeName) && !dropped.contains(typeName)) {
                    if (dropType) {
                        dropped.add(typeName)
                    } else {
                        types[typeName] = TypeSpec.fromJson(typeSpec)
                    }
                }
            }
        }

        return Tuple(types, dropped)
    }

    /**
     * Wrapped SQL update query execution helper.
     */
    fun executeUpdate(conn: Connection, sql: String) {
        q {
            val s = conn.createStatement()
            logger.debug("Attempting to execute SQL update:\n$sql")
            s.executeUpdate(sql)
            conn.commit()
            s.close()
        }
    }

    /**
     * Loads SQL queries from the given file and executes them using the specified connection.
     * @param conn The connection with which to execute the queries.
     * @param filename The name of the resource file from which to load SQL queries.
     */
    fun loadAndExecute(conn: Connection, filename: String)
        = executeUpdate(conn, loadQuery(filename))

    /**
     * Utility/helper function to load a resource file into memory as a string.
     * @param filename The name of the file, relative to the resourcePath, to load.
     */
    fun loadQuery(filename: String): String
        = IOUtils.toString(javaClass.getResourceAsStream(Paths.get(resourcePath, filename).toString()), Charset.defaultCharset())

    /**
     * Generic wrapper for our SQL queries to track exceptions. Translates SQLException's into DatabaseError's.
     * @param query A function to execute within a logged try/catch block.
     */
    fun q(query: () -> Unit) {
        try {
            query.invoke()
        } catch (e: SQLException) {
            logger.error("SQL exception caught while attempting to execute database query", e)
            throw DatabaseError("Error while attempting to execute database query", e)
        }
    }

}
