package com.thanethomson.gcms.errors

class JsonParseError(message: String, original: Throwable? = null): Throwable(message, original)
class JsonBuildError(message: String?): Throwable(message)
class QueryParseError(message: String, original: Throwable? = null): Throwable(message, original)
