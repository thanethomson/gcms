package com.thanethomson.gcms.tests.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.thanethomson.gcms.controllers.CONTENT_TYPES
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

import org.junit.Assert.*;
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap


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
