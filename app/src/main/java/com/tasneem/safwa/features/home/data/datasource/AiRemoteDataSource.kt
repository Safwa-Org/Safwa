package com.tasneem.safwa.features.home.data.datasource

interface AiRemoteDataSource {
    suspend fun getRecommendations(prompt: String): List<String>
}
