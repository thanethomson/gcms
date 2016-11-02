package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.TypeSpec

interface StorageEngine {

    /**
     * Should ensure that all migrations are applied.
     */
    fun applyMigrations()

    /**
     * Retrieves a listing of all of the available types.
     */
    fun getTypes(): Map<String, TypeSpec>

    /**
     * Should create/update a type with the given name and type spec.
     *
     * @param name: The name of the type.
     * @param spec: The description of the type.
     */
    fun upsertType(name: String, spec: TypeSpec)

    /**
     * Retrieves the type spec for the type with the given name.
     *
     * @param name: The name of the type.
     */
    fun getType(name: String): TypeSpec

    /**
     * Should delete the type with the specified name.
     *
     * @param name: The name of the type.
     */
    fun deleteType(name: String)

    /**
     * Should flush all data out of the storage engine.
     */
    fun flush()

    /**
     * Should cleanly shut down the connection to the storage system.
     */
    fun shutdown()

}
