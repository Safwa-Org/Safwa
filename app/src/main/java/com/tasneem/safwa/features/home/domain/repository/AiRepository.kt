package com.tasneem.safwa.features.home.domain.repository

interface AiRepository {
    suspend fun getRecommendedProductIds(prompt: String): List<String>
}
