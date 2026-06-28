package com.tasneem.network.exception

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloException

suspend inline fun <D : Operation.Data, T> safeApiCall(
    crossinline apiCall: suspend () -> ApolloResponse<D>,
    crossinline mapper: (D) -> T
): T {

    val response = try {
        apiCall()
    } catch (_: ApolloException) {
        throw NetworkException()
    }

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