package com.tasneem.safwa.core.ai.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.tasneem.safwa.BuildConfig
import com.tasneem.safwa.core.ai.domain.GenerativeAiService
import javax.inject.Inject

class GenerativeAiServiceImpl @Inject constructor() : GenerativeAiService {
    override suspend fun generateContent(
        prompt: String,
        systemInstruction: String?,
        responseMimeType: String?,
        temperature: Float?
    ): String {
        val model = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType?.let { this.responseMimeType = it }
                temperature?.let { this.temperature = it }
            },
            systemInstruction = systemInstruction?.let { instr ->
                content { text(instr) }
            }
        )
        val response = model.generateContent(prompt)
        return response.text ?: ""
    }
}
