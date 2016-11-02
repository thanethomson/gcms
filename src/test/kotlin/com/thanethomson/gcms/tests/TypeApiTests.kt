package com.thanethomson.gcms.tests

import com.thanethomson.gcms.Application
import com.thanethomson.gcms.storage.StorageEngine
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
        val json = getForJson(restTemplate, "/type")
        assertEquals(0, json.size())
    }

    @Test
    fun testCreateSimpleType() {
        putForJson(restTemplate, "/type/Post", TestData.postType.toJsonString())
        val types = storageEngine.getTypes()
        assertEquals(1, types.size)
        assertTrue(types.containsKey("Post"))
        assertEquals(TestData.postType, types["Post"])
    }

}
