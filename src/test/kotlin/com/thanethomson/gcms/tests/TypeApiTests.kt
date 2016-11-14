package com.thanethomson.gcms.tests

import com.fasterxml.jackson.databind.JsonNode
import com.thanethomson.gcms.Application
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.errors.NotFoundError
import com.thanethomson.gcms.storage.StorageEngine
import com.thanethomson.gcms.tests.utils.TestData
import com.thanethomson.gcms.tests.utils.deleteForJson
import com.thanethomson.gcms.tests.utils.getForJson
import com.thanethomson.gcms.tests.utils.putForJson
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import org.junit.Assert.*;
import org.junit.Before
import org.springframework.test.context.ActiveProfiles


@RunWith(SpringJUnit4ClassRunner::class)
@ActiveProfiles("test")
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TypeApiTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var storageEngine: StorageEngine

    @Before
    fun setup() {
        // make sure we clear out the database prior to a test
        storageEngine.flush()
    }

    @Test
    fun testListEmptyTypes() {
        val json = getTypesList()
        assertEquals(0, json.size())
    }

    @Test
    fun testCreateSimpleType() {
        putSimpleType()
    }

    @Test
    fun testUpsertSimpleType() {
        // do it twice
        putSimpleType()
        // should be just as nice
        putSimpleType()
    }

    @Test
    fun testCreateSimpleTypeThenGet() {
        putSimpleType()
        val json = getTypesList()
        assertEquals(1, json.size())
        assertTrue(json.hasNonNull("Post"))
        // convert this JSON object to a TypeSpec
        val createdTypeSpec = TypeSpec.fromJson(json.get("Post"))
        assertEquals(TestData.postType, createdTypeSpec)
    }

    @Test
    fun testCreateThenDeleteSimpleType() {
        putSimpleType()
        deleteForJson(restTemplate, "/type/Post")
        typeShouldNotExist("Post")
    }

    fun getTypesList(): JsonNode = getForJson(restTemplate, "/type")

    fun putSimpleType() {
        putForJson(restTemplate, "/type/Post", TestData.postType.toJsonString())
        val types = storageEngine.getTypes()
        assertEquals(1, types.size)
        assertTrue(types.containsKey("Post"))
        assertEquals(TestData.postType, types["Post"])
    }

    fun typeShouldNotExist(name: String) {
        try {
            storageEngine.getType(name)
            fail("Type $name should not exist, but it does")
        } catch (e: NotFoundError) {
            // success
        }
    }

}
