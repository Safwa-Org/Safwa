package com.tasneem.safwa.features.home.data.datasource

import com.google.ai.client.generativeai.GenerativeModel
import com.tasneem.safwa.BuildConfig
import javax.inject.Inject

class AiRemoteDataSourceImpl @Inject constructor() : AiRemoteDataSource {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    override suspend fun getRecommendations(prompt: String): List<String> {
        val response = generativeModel.generateContent(prompt)
        val responseText = response.text?.trim() ?: ""
        return responseText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
