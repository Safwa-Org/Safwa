package com.tasneem.network.exception

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException

suspend inline fun <D : Operation.Data, T> safeApiCall(
    crossinline apiCall: suspend () -> ApolloResponse<D>,
    crossinline mapper: (D) -> T
): T {

    val response = try {
        apiCall()
    } catch (e: ApolloException) {
        throw e.toAppException()
    }

    response.exception?.let { throw it.toAppException() }

    if (response.hasErrors()) {

        throw GraphQlException(
            response.errors?.joinToString {
                it.message
            }.orEmpty()
        )
    }

    val data = response.data
        ?: throw EmptyResponseException()

    return mapper(data)
}

fun ApolloException.toAppException(): AppException = when (this) {
    is ApolloNetworkException -> when (platformCause ?: cause) {
        is UnknownHostException,
        is ConnectException,
        is SocketException -> NoInternetException()

        else -> NetworkException()
    }

    else -> NetworkException()
}
