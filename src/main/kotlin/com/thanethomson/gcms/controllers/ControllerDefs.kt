package com.thanethomson.gcms.controllers

import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap


object CONTENT_TYPES {

    @JvmStatic val APPLICATION_JSON = LinkedMultiValueMap<String, String>()
    init {
        APPLICATION_JSON.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)
    }

}

