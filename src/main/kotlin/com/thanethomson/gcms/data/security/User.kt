package com.thanethomson.gcms.data.security

import com.thanethomson.gcms.enums.Privilege
import java.util.*

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val firstName: String?,
    val lastName: String?,
    val created: Date,
    val privileges: Set<Privilege>
)
