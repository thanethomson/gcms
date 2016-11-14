package com.thanethomson.gcms.errors

class NotFoundError(message: String?) : Throwable(message)
class FieldDoesNotExistError(message: String?): Throwable(message)
class TypeSpecError(message: String?): Throwable(message)
