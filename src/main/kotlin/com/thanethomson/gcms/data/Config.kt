package com.thanethomson.gcms.data

import com.thanethomson.gcms.storage.StorageEngine

/**
 * Configuration object to be passed through to the various API functions.
 */
data class Config(
        val storage: StorageEngine
)
