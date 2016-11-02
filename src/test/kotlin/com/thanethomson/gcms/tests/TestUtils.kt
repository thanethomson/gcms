package com.thanethomson.gcms.tests

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

import org.junit.Assert.*;
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap


object CONTENT_TYPES {

    @JvmStatic val APPLICATION_JSON = LinkedMultiValueMap<String, String>()
    init {
        APPLICATION_JSON.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)
    }

}

fun parseJson(s: String): JsonNode = ObjectMapper().readTree(s)

fun getForJson(t: TestRestTemplate, url: String, expectedStatus: HttpStatus = HttpStatus.OK): JsonNode {
    val response = t.getForEntity(url, String::class.java)
    assertEquals(expectedStatus, response.statusCode)
    return parseJson(response.body)
}

fun putForJson(t: TestRestTemplate, url: String, data: String, expectedStatus: HttpStatus = HttpStatus.OK): JsonNode? {
    val response = t.exchange(
            url,
            HttpMethod.PUT,
            HttpEntity<String>(data, CONTENT_TYPES.APPLICATION_JSON),
            String::class.java
    )
    assertEquals(expectedStatus, response.statusCode)
    return if (response.body.length > 0) parseJson(response.body) else null
}

fun deleteForJson(t: TestRestTemplate, url: String, expectedStatus: HttpStatus = HttpStatus.OK): JsonNode? {
    val response = t.exchange(
            url,
            HttpMethod.DELETE,
            HttpEntity<String>(""),
            String::class.java
    )
    assertEquals(expectedStatus, response.statusCode)
    return if (response.body.length > 0) parseJson(response.body) else null
}
