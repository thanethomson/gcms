package com.thanethomson.gcms.controllers

import com.google.common.util.concurrent.ListeningExecutorService
import com.thanethomson.gcms.data.SuccessMessage
import com.thanethomson.gcms.data.TypeSpec
import com.thanethomson.gcms.data.makeJsonStringFromTypeList
import com.thanethomson.gcms.storage.StorageEngine
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
            }
        )

    @RequestMapping(path = arrayOf("/{name}"), method = arrayOf(RequestMethod.PUT))
    fun upsertType(@PathVariable name: String, @RequestBody body: String)
        = executeControllerAsync(
            executor,
            Callable { storageEngine.upsertType(name, TypeSpec(body)) },
            Function { result ->
                makeSimpleResponseEntity(
                    SuccessMessage("OK").toJsonString(),
                    HttpStatus.OK
                )
            }
        )

    @RequestMapping(path = arrayOf("/{name}"), method = arrayOf(RequestMethod.DELETE))
    fun deleteType(@PathVariable name: String)
        = executeControllerAsync(
            executor,
            Callable { storageEngine.deleteType(name) },
            Function { result ->
                makeSimpleResponseEntity(
                    SuccessMessage("OK").toJsonString(),
                    HttpStatus.OK
                )
            }
        )

}