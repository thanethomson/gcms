package com.thanethomson.gcms.utils

import java.util.*

class StringValidation {

    companion object {
        @JvmStatic val SQL_VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"
        @JvmStatic val SQL_VALID_CHAR_SET = HashSet<Char>(SQL_VALID_CHARS.toHashSet())

        @JvmStatic fun isValidSqlId(id: String): Boolean {
            for (ch in id) {
                if (!SQL_VALID_CHAR_SET.contains(ch))
                    return false
            }
            return true
        }

    }

}