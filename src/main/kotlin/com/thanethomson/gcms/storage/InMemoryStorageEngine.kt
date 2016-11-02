package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.TypeSpec
import com.thanethomson.gcms.enums.FieldType
import com.thanethomson.gcms.errors.FieldDoesNotExistError
import com.thanethomson.gcms.errors.NotFoundError
import java.util.*

/**
 * Dummy class for in-memory unit testing. Do NOT use in production.
 */
class InMemoryStorageEngine: StorageEngine {

    private val types = HashMap<String, TypeSpec>()

    override fun applyMigrations() {
        // nothing needed here
    }

    override fun getTypes(): Map<String, TypeSpec> = types

    override fun upsertType(name: String, spec: TypeSpec) {
        // first perform some validation
        for ((fieldName, field) in spec.fields) {
            // if it's a foreign key
            if (field.type == FieldType.FOREIGN_KEY) {
                // check that the field it's referencing already exists
                if (field.references == null || field.references !in types) {
                    throw FieldDoesNotExistError("Foreign key field does not exist: ${field.references}")
                }
            }
        }
        types[name] = spec
    }

    override fun getType(name: String): TypeSpec {
        val type: TypeSpec? = if (name in types) types[name] else null
        if (type != null) {
            return type
        }
        throw NotFoundError("Cannot find type: $name")
    }

    override fun deleteType(name: String) {
        if (name in types) {
            types.remove(name)
        } else {
            throw NotFoundError("Cannot find type: $name")
        }
    }

    override fun flush() {
        types.clear()
    }

    override fun shutdown() {
        // nothing needed here
    }

}