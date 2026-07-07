package com.tasneem.safwa.core.ai.data.remote

import com.tasneem.safwa.core.ai.data.remote.dto.OpenRouterRequest
import com.tasneem.safwa.core.ai.data.remote.dto.OpenRouterResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("api/v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") authHeader: String,
        @Header("HTTP-Referer") referer: String = "http://localhost",
        @Header("X-Title") title: String = "Safwa",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}
