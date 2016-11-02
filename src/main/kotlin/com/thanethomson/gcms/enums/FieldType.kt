package com.thanethomson.gcms.enums

import java.util.*

enum class FieldType constructor(val id: String) {
    STRING("string"),
    INT("int"),
    BIGINT("bigint"),
    TEXT("text"),
    FLOAT("float"),
    BOOLEAN("bool"),
    DATETIME("datetime"),
    DATE("date"),
    FOREIGN_KEY("fk");

    companion object {
        @JvmStatic val fieldTypesById = HashMap<String, FieldType>()

        init {
            for (fieldType in FieldType.values()) {
                fieldTypesById[fieldType.id] = fieldType
            }
        }

        @JvmStatic fun fromId(id: String): FieldType {
            val type: FieldType? = fieldTypesById.getOrDefault(id, null)
            if (type != null) {
                return type
            }
            throw IllegalArgumentException("Unknown field type: $id")
        }
    }
}
