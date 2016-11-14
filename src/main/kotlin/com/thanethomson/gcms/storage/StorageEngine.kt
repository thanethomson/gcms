package com.thanethomson.gcms.storage

import com.thanethomson.gcms.data.storage.StorageQuery
import com.thanethomson.gcms.data.storage.TypeInstance
import com.thanethomson.gcms.data.storage.TypeSpec

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
     * @param name The name of the type.
     * @param spec The description of the type.
     */
    fun upsertType(name: String, spec: TypeSpec)

    /**
     * Retrieves the type spec for the type with the given name.
     *
     * @param name The name of the type.
     */
    fun getType(name: String): TypeSpec

    /**
     * Should delete the type with the specified name.
     *
     * @param name The name of the type.
     */
    fun deleteType(name: String)

    /**
     * Create an instance of a type from the given field data.
     *
     * @param type The type of which to create an instance.
     * @param fieldValues The values for each of the fields for this instance.
     * @return The newly created instance.
     */
    fun createInstance(type: String, fieldValues: Map<String, Any?>): TypeInstance

    /**
     * Updates the specified fields for the specified type instance.
     *
     * @param type The type of the instance to be updated.
     * @param id The unique ID of the instance to be updated.
     * @param fieldValues The specific field values to be updated.
     * @return If found, returns the updated instance.
     *
     * @exception NotFoundError If the instance with the specified ID cannot be found.
     */
    fun updateInstance(type: String, id: String, fieldValues: Map<String, Any?>): TypeInstance

    /**
     * Retrieves the instance of the specified type with the given ID.
     *
     * @param type The type of the instance to be fetched.
     * @param id The ID of the instance to be fetched.
     * @param relatedDepth The depth to which related objects should be fetched (default: 0).
     * @return If found, returns the instance.
     *
     * @exception NotFoundError If the instance with the specified ID cannot be found.
     */
    fun getInstance(type: String, id: String, relatedDepth: Int = 0): TypeInstance

    /**
     * Attempts to retrieve all instances of the specified type.
     */
    fun getAllInstances(
        type: String,
        orderBy: String? = null,   // default: no ordering
        orderAsc: Boolean = true,  // default: if ordered, order ascending
        limit: Int? = null,        // default: no limit
        relatedDepth: Int? = 0     // default: no related objects (null = unlimited)
    ): List<TypeInstance>

    /**
     * Attempts to find all instances of the specified type matching the given query.
     *
     * @param type The type of instances through which to search.
     * @param query The query to execute.
     * @param orderBy The field by which to order the instances.
     * @param orderAsc Should the ordering be ascending?
     * @param limit The maximum number of results to return.
     * @param relatedDepth The maximum depth to which to fetch and parse related objects.
     * @return On success, a list containing the found instances.
     */
    fun findInstances(
        type: String,
        query: StorageQuery,
        orderBy: String? = null,   // default: no ordering
        orderAsc: Boolean = true,  // default: if ordered, order ascending
        limit: Int? = null,        // default: no limit
        relatedDepth: Int? = 0     // default: no related objects (null = unlimited)
    ): List<TypeInstance>

    /**
     * Attempts to delete the specified instance of the given type.
     *
     * @param type The type of instance to be deleted.
     * @param id The ID of the instance to be deleted.
     *
     * @exception NotFoundError If the instance with the specified ID cannot be found.
     */
    fun deleteInstance(type: String, id: String)

    /**
     * Should flush all data out of the storage engine.
     */
    fun flush()

    /**
     * Should cleanly shut down the connection to the storage system.
     */
    fun shutdown()

}
