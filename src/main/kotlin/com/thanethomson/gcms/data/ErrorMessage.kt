package com.thanethomson.gcms.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

data class ErrorMessage(val msg: String) {

    fun toJson(): JsonNode {
        val json = JsonNodeFactory.instance.objectNode()
        json.put("error", msg)
        return json
    }

    fun toJsonString(): String = toJson().toString()

}
