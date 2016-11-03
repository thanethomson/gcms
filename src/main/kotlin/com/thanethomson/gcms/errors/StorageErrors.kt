package com.thanethomson.gcms.errors

class NotFoundError(message: String?) : Throwable(message)
class FieldDoesNotExistError(message: String?): Throwable(message)
class JsonParseError(message: String, original: Throwable? = null): Throwable(message, original)
