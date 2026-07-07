package com.tasneem.safwa.features.aichat.domain.repository

import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery

interface AiSearchRepository {
    suspend fun translateToShopifyQuery(userInput: String): SmartSearchQuery
}
