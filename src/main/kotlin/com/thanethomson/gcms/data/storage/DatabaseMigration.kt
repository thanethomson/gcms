package com.thanethomson.gcms.data.storage

import java.util.*

/**
 * Helps us keep track of database migrations of type specs.
 */
data class DatabaseMigration(
    val id: String,
    val typeName: String,
    val typeSpec: String?,
    val dropTable: Boolean,
    val byUser: String,
    val created: Date,
    val applied: Date?
)
