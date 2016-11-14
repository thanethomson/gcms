package com.thanethomson.gcms.data.messaging

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

data class SuccessMessage(val message: String) {

    fun toJson(): JsonNode {
        val json = JsonNodeFactory.instance.objectNode()
        json.put("message", message)
        return json
    }

    fun toJsonString(): String = toJson().toString()

}