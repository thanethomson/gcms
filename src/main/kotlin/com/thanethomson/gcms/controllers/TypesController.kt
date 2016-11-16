package com.thanethomson.gcms.controllers

import com.google.common.util.concurrent.ListeningExecutorService
import com.thanethomson.gcms.data.messaging.SuccessMessage
import com.thanethomson.gcms.data.storage.TypeSpec
import com.thanethomson.gcms.data.makeJsonStringFromTypeList
import com.thanethomson.gcms.storage.StorageEngine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.Callable
import java.util.function.Function

@RestController
@RequestMapping(path = arrayOf("/type"))
class TypesController @Autowired constructor(
    val executor: ListeningExecutorService,
    val storageEngine: StorageEngine
) {

    companion object {
        @JvmStatic val logger: Logger = LoggerFactory.getLogger(TypesController::class.java)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun listTypes(): DeferredResult<ResponseEntity<String>>
        = executeControllerAsync(
            executor,
            Callable { storageEngine.getTypes() },
            Function { result ->
                makeSimpleResponseEntity(
                    makeJsonStringFromTypeList(result),
                    HttpStatus.OK
                )
            },
            logger
        )

    @RequestMapping(path = arrayOf("/{name}"), method = arrayOf(RequestMethod.PUT))
    fun upsertType(@PathVariable name: String, @RequestBody body: String): DeferredResult<ResponseEntity<String>>
        = executeControllerAsync(
            executor,
            Callable { storageEngine.upsertType(name, TypeSpec.fromJson(body)) },
            Function { result ->
                makeSimpleResponseEntity(
                    SuccessMessage("OK").toJsonString(),
                    HttpStatus.OK
                )
            },
            logger
        )

    @RequestMapping(path = arrayOf("/{name}"), method = arrayOf(RequestMethod.DELETE))
    fun deleteType(@PathVariable name: String): DeferredResult<ResponseEntity<String>>
        = executeControllerAsync(
            executor,
            Callable { storageEngine.deleteType(name) },
            Function { result ->
                makeSimpleResponseEntity(
                    SuccessMessage("OK").toJsonString(),
                    HttpStatus.OK
                )
            },
            logger
        )

}