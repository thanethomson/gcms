package com.thanethomson.gcms.tests

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.thanethomson.gcms.Application
import com.thanethomson.gcms.data.storage.FieldSpec
import com.thanethomson.gcms.data.storage.TypeInstance
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.enums.FieldType
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.*

import org.junit.Assert.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.ParseException

@RunWith(SpringJUnit4ClassRunner::class)
@ActiveProfiles("test")
@SpringBootTest(classes = arrayOf(Application::class))
class TypeInstanceParsingTests {

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(TypeInstanceParsingTests::class.java)

        @JvmStatic val VALID_POST1 = "{\"title\": \"Valid post\", \"posted\": \"2016-11-15T10:36:25+02:00\", \"body\": \"Post body\"}"
        @JvmStatic val VALID_POST2 = "{\"title\": \"Valid post, no body\", \"posted\": \"2016-11-15T10:36:25+02:00\"}"
        @JvmStatic val VALID_POST3 = "{\"title\": \"Valid post, only title\"}"
        @JvmStatic val VALID_POST4 = "{\"title\": \"Valid post, only title\", \"extraField\": \"This will be ignored\"}"

        @JvmStatic val INVALID_POST1 = "{\"posted\": \"invalid date\"}"

        @JvmStatic val VALID_COMPLEX_POST = "{\"id\": \"post01\", " +
            "\"title\": \"A post with an author\", " +
            "\"posted\": \"2016-11-15T10:36:25+02:00\", " +
            "\"body\": \"Post body\"," +
            "\"author\": {\"id\": \"author01\", \"name\": \"Michael Anderson\", \"active\": true} }"

        @JvmStatic val TYPESPECS = HashMap<String, TypeSpec>()

        init {
            val postTypeFields = HashMap<String, FieldSpec>()
            postTypeFields["title"] = FieldSpec(FieldType.STRING)
            postTypeFields["posted"] = FieldSpec(FieldType.DATETIME)
            postTypeFields["body"] = FieldSpec(FieldType.STRING)
            postTypeFields["author"] = FieldSpec(FieldType.FOREIGN_KEY, references = "Author")
            TYPESPECS.put("Post", TypeSpec(postTypeFields))

            val authorTypeFields = HashMap<String, FieldSpec>()
            authorTypeFields["name"] = FieldSpec(FieldType.STRING)
            authorTypeFields["active"] = FieldSpec(FieldType.BOOLEAN, default = false)
            TYPESPECS.put("Author", TypeSpec(authorTypeFields))
        }
    }

    @Test
    fun testParseSimpleValidPosts() {
        var instance = TypeInstance.fromJson(TYPESPECS, "Post", VALID_POST1)
        val fieldValues = HashMap<String, Any?>()
        fieldValues["title"] = "Valid post"
        fieldValues["posted"] = ISO8601DateFormat().parse("2016-11-15T10:36:25+02:00")
        fieldValues["body"] = "Post body"
        fieldValues["author"] = null
        assertEquals(
            TypeInstance(
                "Post",
                null,
                fieldValues
            ),
            instance
        )

        instance = TypeInstance.fromJson(TYPESPECS, "Post", VALID_POST2)
        fieldValues.clear()
        fieldValues["title"] = "Valid post, no body"
        fieldValues["posted"] = ISO8601DateFormat().parse("2016-11-15T10:36:25+02:00")
        fieldValues["body"] = null
        fieldValues["author"] = null
        assertEquals(
            TypeInstance(
                "Post",
                null,
                fieldValues
            ),
            instance
        )

        instance = TypeInstance.fromJson(TYPESPECS, "Post", VALID_POST3)
        fieldValues.clear()
        fieldValues["title"] = "Valid post, only title"
        fieldValues["posted"] = null
        fieldValues["body"] = null
        fieldValues["author"] = null
        assertEquals(
            TypeInstance(
                "Post",
                null,
                fieldValues
            ),
            instance
        )

        instance = TypeInstance.fromJson(TYPESPECS, "Post", VALID_POST4)
        fieldValues.clear()
        fieldValues["title"] = "Valid post, only title"
        fieldValues["posted"] = null
        fieldValues["body"] = null
        fieldValues["author"] = null
        assertEquals(
            TypeInstance(
                "Post",
                null,
                fieldValues
            ),
            instance
        )
    }

    @Test
    fun testParseSimpleInvalidPosts() {
        try {
            TypeInstance.fromJson(TYPESPECS, "Post", INVALID_POST1)
            fail("Parsing of INVALID_POST1 should fail due to invalid date format")
        } catch (e: ParseException) {
            logger.debug("Successfully caught parsing exception: $e", e)
        }
    }

    @Test
    fun testParseComplexValidPost() {
        val instance = TypeInstance.fromJson(TYPESPECS, "Post", VALID_COMPLEX_POST)
        val postFieldValues = HashMap<String, Any?>()
        val authorFieldValues = HashMap<String, Any?>()

        authorFieldValues["name"] = "Michael Anderson"
        authorFieldValues["active"] = true

        postFieldValues["title"] = "A post with an author"
        postFieldValues["posted"] = ISO8601DateFormat().parse("2016-11-15T10:36:25+02:00")
        postFieldValues["body"] = "Post body"
        postFieldValues["author"] = TypeInstance(
            "Author",
            "author01",
            authorFieldValues
        )

        assertEquals(
            TypeInstance(
                "Post",
                "post01",
                postFieldValues
            ),
            instance
        )
    }

}