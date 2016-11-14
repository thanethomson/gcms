package com.thanethomson.gcms.tests.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.thanethomson.gcms.controllers.CONTENT_TYPES
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

import org.junit.Assert.*;
import org.slf4j.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod


fun parseJson(s: String): JsonNode = ObjectMapper().readTree(s)

fun getForJson(t: TestRestTemplate, url: String, expectedStatus: HttpStatus = HttpStatus.OK, logger: Logger? = null): JsonNode {
    val response = t.getForEntity(url, String::class.java)
    logger?.debug("Got response for GET request, code ${response.statusCode}:\n${response.body}")
    assertEquals(expectedStatus, response.statusCode)
    return parseJson(response.body)
}

fun putForJson(t: TestRestTemplate, url: String, data: String, expectedStatus: HttpStatus = HttpStatus.OK, logger: Logger? = null): JsonNode? {
    logger?.debug("Attempting to send PUT request to $url with data:\n$data")
    val response = t.exchange(
            url,
            HttpMethod.PUT,
            HttpEntity<String>(data, CONTENT_TYPES.APPLICATION_JSON),
            String::class.java
    )
    logger?.debug("Got response for PUT request, code ${response.statusCode}:\n${response.body}")
    assertEquals(expectedStatus, response.statusCode)
    return if (response.body.length > 0) parseJson(response.body) else null
}

fun deleteForJson(t: TestRestTemplate, url: String, expectedStatus: HttpStatus = HttpStatus.OK, logger: Logger? = null): JsonNode? {
    val response = t.exchange(
            url,
            HttpMethod.DELETE,
            HttpEntity<String>(""),
            String::class.java
    )
    logger?.debug("Got response for DELETE request, code ${response.statusCode}:\n${response.body}")
    assertEquals(expectedStatus, response.statusCode)
    return if (response.body.length > 0) parseJson(response.body) else null
}
