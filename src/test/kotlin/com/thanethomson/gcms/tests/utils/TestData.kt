package com.thanethomson.gcms.tests.utils

import com.thanethomson.gcms.data.storage.FieldSpec
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.enums.FieldType
import java.util.*

class TestData {

    companion object {
        val postType: TypeSpec
        val postTypeFk: TypeSpec
        val postTypeFields = HashMap<String, FieldSpec>()
        val postTypeFieldsFk = HashMap<String, FieldSpec>()

        val authorType: TypeSpec
        val authorTypeFields = HashMap<String, FieldSpec>()

        init {
            // initialise our test data
            postTypeFields["title"] = FieldSpec(FieldType.STRING, maxSize = 300)
            postTypeFields["posted"] = FieldSpec(FieldType.DATETIME)
            postTypeFields["body"] = FieldSpec(FieldType.TEXT)
            postType = TypeSpec(postTypeFields)

            postTypeFieldsFk["title"] = FieldSpec(FieldType.STRING, maxSize = 300)
            postTypeFieldsFk["author"] = FieldSpec(FieldType.FOREIGN_KEY, references = "Author")
            postTypeFieldsFk["posted"] = FieldSpec(FieldType.DATETIME)
            postTypeFieldsFk["body"] = FieldSpec(FieldType.TEXT)
            postTypeFk = TypeSpec(postTypeFieldsFk)

            authorTypeFields["name"] = FieldSpec(FieldType.STRING, maxSize = 300)
            authorTypeFields["email"] = FieldSpec(FieldType.STRING, maxSize = 500)
            authorType = TypeSpec(authorTypeFields)
        }
    }

}
