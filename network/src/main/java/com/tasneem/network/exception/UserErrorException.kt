package com.tasneem.network.exception

class UserErrorException(val messages: List<String>) : AppException() {
    override val message: String
        get() = messages.joinToString(separator = "\n").ifEmpty { "Shopify rejected the request" }
}
