package com.tasneem.safwa.core.ai.data

import com.tasneem.safwa.BuildConfig
import com.tasneem.safwa.core.ai.data.remote.OpenRouterApi
import com.tasneem.safwa.core.ai.data.remote.dto.Message
import com.tasneem.safwa.core.ai.data.remote.dto.OpenRouterRequest
import com.tasneem.safwa.core.ai.data.remote.dto.ResponseFormat
import com.tasneem.safwa.core.ai.domain.ChatBotAiService
import javax.inject.Inject

class ChatBotAiServiceImpl @Inject constructor(
    private val openRouterApi: OpenRouterApi
) : ChatBotAiService {

    override suspend fun generateContent(
        prompt: String,
        systemInstruction: String?,
        responseMimeType: String?,
        temperature: Float?
    ): String {
        val messages = mutableListOf<Message>()
        
        if (!systemInstruction.isNullOrBlank()) {
            messages.add(Message(role = "system", content = systemInstruction))
        }
        
        messages.add(Message(role = "user", content = prompt))

        val request = OpenRouterRequest(
            model = "google/gemma-3-12b-it",
            messages = messages,
            temperature = temperature,
            response_format = if (responseMimeType == "application/json") ResponseFormat(type = "json_object") else null
        )

        return try {
            val response = openRouterApi.generateContent(
                authHeader = BuildConfig.OPEN_ROUTER_API_KEY,
                request = request
            )
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
