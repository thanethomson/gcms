package com.thanethomson.gcms.storage

import java.util.*

/**
 * Common functionality across all storage engines.
 */
abstract class AbstractStorageEngine: StorageEngine {

    /**
     * Helper function to generate unique string IDs for database entries. At the moment, this
     * uses Java's native UUID.randomUUID() function.
     */
    fun generateId(): String
        = UUID.randomUUID().toString()

}