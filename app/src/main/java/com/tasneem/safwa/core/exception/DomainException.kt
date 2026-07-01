package com.tasneem.safwa.core.exception

sealed class DomainException : Exception() {
    class NoInternet : DomainException()
    class NetworkError : DomainException()
    data class ServerError(val reason: String) : DomainException()
    class NotFound : DomainException()
    class Unknown : DomainException()
}
