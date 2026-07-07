package com.tasneem.safwa.core.ai.data.remote.dto

data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Float? = null,
    val response_format: ResponseFormat? = null
)

data class ResponseFormat(
    val type: String
)

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<Choice>? = null
) {
    val text: String
        get() = choices?.firstOrNull()?.message?.content ?: ""
}

data class Choice(
    val message: Message? = null
)
