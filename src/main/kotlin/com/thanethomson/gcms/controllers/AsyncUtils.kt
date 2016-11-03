package com.thanethomson.gcms.controllers

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListeningExecutorService
import com.thanethomson.gcms.data.ErrorMessage
import com.thanethomson.gcms.errors.FieldDoesNotExistError
import com.thanethomson.gcms.errors.JsonParseError
import com.thanethomson.gcms.errors.NotFoundError
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.Callable
import java.util.function.Function


/**
 * Helper function to speed up async function coding in Spring Boot controllers.
 *
 * @param executor        The Google Guava ListeningExecutorService to use when submitting our function.
 * @param fn              The (long-running) function to execute.
 * @param successCallback The callback function to translate the result of the execution into a ResponseEntity.
 * @param logger          An optional logger for error logging.
 *
 * @return A DeferredResult which can be passed to a Spring Boot controller.
 */
fun <T> executeControllerAsync(
    executor: ListeningExecutorService,
    fn: Callable<T>,
    successCallback: Function<T, ResponseEntity<String>>,
    logger: Logger? = null): DeferredResult<ResponseEntity<String>> {

    val deferred = DeferredResult<ResponseEntity<String>>()
    val future = executor.submit(fn)
    Futures.addCallback(future, object: FutureCallback<T> {
        override fun onSuccess(result: T?) {
            try {
                deferred.setResult(successCallback.apply(result))
            } catch (t: Throwable) {
                onFailure(t)
            }
        }

        override fun onFailure(t: Throwable?) {
            logger?.error("Request failure", t ?: Exception("Unknown exception"))

            val result: ResponseEntity<String>

            if (t != null) {
                if (t is NotFoundError) {
                    result = makeErrorResponseEntity(t.message ?: "Not found", HttpStatus.NOT_FOUND)
                } else if (t is FieldDoesNotExistError) {
                    result = makeErrorResponseEntity(t.message ?: "Field does not exist", HttpStatus.BAD_REQUEST)
                } else if (t is JsonParseError) {
                    result = makeErrorResponseEntity(t.message ?: "Invalid JSON data in request", HttpStatus.BAD_REQUEST)
                } else {
                    result = makeErrorResponseEntity(t.message ?: "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)
                }
            } else {
                result = makeErrorResponseEntity("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)
            }

            deferred.setResult(result)
        }
    })
    return deferred
}

fun makeSimpleResponseEntity(content: String, status: HttpStatus): ResponseEntity<String>
    = ResponseEntity(
        content,
        CONTENT_TYPES.APPLICATION_JSON,
        status
    )

fun makeErrorResponseEntity(msg: String, status: HttpStatus): ResponseEntity<String>
    = ResponseEntity(
        ErrorMessage(msg).toJsonString(),
        CONTENT_TYPES.APPLICATION_JSON,
        status
    )
