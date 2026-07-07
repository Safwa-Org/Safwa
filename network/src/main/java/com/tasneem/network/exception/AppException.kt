package com.tasneem.network.exception

sealed class AppException : Exception()

class NetworkException : AppException()

class NoInternetException : AppException()

class GraphQlException(val errors: String) : AppException()

class EmptyResponseException : AppException()

class UnauthenticatedException : AppException()

class UnknownException(cause: Throwable) : AppException()