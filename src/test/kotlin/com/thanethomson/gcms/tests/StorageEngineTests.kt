package com.thanethomson.gcms.tests

import com.thanethomson.gcms.Application
import com.thanethomson.gcms.errors.FieldDoesNotExistError
import com.thanethomson.gcms.storage.StorageEngine
import com.thanethomson.gcms.tests.utils.TestData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner::class)
@ActiveProfiles("test")
@SpringBootTest(classes = arrayOf(Application::class))
class StorageEngineTests {

    @Autowired
    lateinit var storageEngine: StorageEngine

    @Before
    fun setup() {
        storageEngine.flush()
    }

    /**
     * Makes sure the storage is empty after a flush (assuming flush is called before each test).
     */
    @Test
    fun testStorageEmpty() {
        assertEquals(0, storageEngine.getTypes().size)
    }

    @Test
    fun testSimpleTypeListing() {
        storageEngine.upsertType("Post", TestData.postType)
        val types = storageEngine.getTypes()
        assertEquals(1, types.size)
        assertTrue(types.containsKey("Post"))
        assertEquals(TestData.postType, types["Post"])
    }

    @Test
    fun testComplexTypeListing() {
        storageEngine.upsertType("Author", TestData.authorType)
        storageEngine.upsertType("Post", TestData.postTypeFk)

        val types = storageEngine.getTypes()
        assertEquals(2, types.size)
        assertTrue(types.containsKey("Author") && types.containsKey("Post"))
        assertEquals(TestData.authorType, types["Author"])
        assertEquals(TestData.postTypeFk, types["Post"])
    }

    @Test
    fun testIncorrectTypeCreationOrder() {
        try {
            // try to create the post type without having created the Author class yet
            storageEngine.upsertType("Post", TestData.postTypeFk)
            fail("Should throw exception here")
        } catch (e: FieldDoesNotExistError) {
            // do nothing
        }
    }

    @Test
    fun testIncorrectTypeCreationOrderWithUpdate() {
        // first correctly create the Post type
        storageEngine.upsertType("Post", TestData.postType)
        assertEquals(1, storageEngine.getTypes().size)

        try {
            // try to create the post type without having created the Author class yet
            storageEngine.upsertType("Post", TestData.postTypeFk)
            fail("Should throw exception here")
        } catch (e: FieldDoesNotExistError) {
            // do nothing
        }

        // now create the Author type
        storageEngine.upsertType("Author", TestData.authorType)
        // now this should pass
        storageEngine.upsertType("Post", TestData.postTypeFk)

        val types = storageEngine.getTypes()
        assertEquals(2, types.size)
        assertTrue(types.containsKey("Author") && types.containsKey("Post"))
        assertEquals(TestData.authorType, types["Author"])
        assertEquals(TestData.postTypeFk, types["Post"])
    }

}