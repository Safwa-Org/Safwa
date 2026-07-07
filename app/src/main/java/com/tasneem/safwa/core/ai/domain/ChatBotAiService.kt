package com.tasneem.safwa.core.ai.domain

interface ChatBotAiService {
    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        responseMimeType: String? = null,
        temperature: Float? = null
    ): String
}
