package com.thanethomson.gcms.enums

import java.util.*

enum class Privilege(val title: String) {
    CREATE_USER("Create users"),
    READ_USER("Read users"),
    UPDATE_USER("Update users"),
    DELETE_USER("Delete users"),

    CREATE_TYPE("Create types"),
    READ_TYPE("Read types"),
    UPDATE_TYPE("Update types"),
    DELETE_TYPE("Delete types"),

    CREATE_INSTANCE("Create instances"),
    READ_INSTANCE("Read instances"),
    UPDATE_INSTANCE("Update instances"),
    DELETE_INSTANCE("Delete instances");

    companion object {
        @JvmStatic val ADMIN = HashSet<Privilege>()
        @JvmStatic val CREATOR = HashSet<Privilege>()
        @JvmStatic val CONSUMER = HashSet<Privilege>()

        init {
            // admin gets all privileges
            ADMIN.addAll(Privilege.values())

            // content creators get the ability to manage types and instances, as well as viewing
            // information about other users
            CREATOR.addAll(listOf(
                READ_USER,
                CREATE_TYPE,
                READ_TYPE,
                UPDATE_TYPE,
                DELETE_TYPE,
                CREATE_INSTANCE,
                READ_INSTANCE,
                UPDATE_INSTANCE,
                DELETE_INSTANCE
            ))

            // content consumers only get the ability to read types and instances
            CONSUMER.addAll(listOf(
                READ_TYPE,
                READ_INSTANCE
            ))
        }
    }
}
