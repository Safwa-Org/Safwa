package com.tasneem.network.exception

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeRestApiCall(apiCall: suspend () -> T): T {
    return try {
        val response = apiCall()
        if (com.tasneem.safwa.network.BuildConfig.DEBUG) {
            android.util.Log.d("JSON_RESPONSE", response.toString())
        }
        response  // was calling apiCall() again here, doubling the network hit
    } catch (e: HttpException) {
        if (com.tasneem.safwa.network.BuildConfig.DEBUG) {
            android.util.Log.e("API_ERROR", "HTTP Code: ${e.code()}, Message: ${e.message()}")
        }
        throw NetworkException()
    } catch (e: Exception) {
        if (com.tasneem.safwa.network.BuildConfig.DEBUG) {
            android.util.Log.e("API_ERROR", "General error: ${e.localizedMessage}")
        }
        throw UnknownException(e)
    }
}