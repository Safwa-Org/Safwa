package com.tasneem.network.exception

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeRestApiCall(apiCall: suspend () -> T): T {
    return try {
        val response = apiCall()
        android.util.Log.d("JSON_RESPONSE", response.toString())
        response  // was calling apiCall() again here, doubling the network hit
    } catch (e: HttpException) {
        android.util.Log.e("API_ERROR", "HTTP Code: ${e.code()}, Message: ${e.message()}")
        throw NetworkException()
    } catch (e: Exception) {
        android.util.Log.e("API_ERROR", "General error: ${e.localizedMessage}")
        throw UnknownException(e)
    }
}