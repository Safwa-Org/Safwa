package com.tasneem.safwa.features.home.domain.repository

import com.tasneem.safwa.features.home.domain.model.AiSearchQuery
import com.tasneem.safwa.features.home.domain.model.UserShoppingHistory

interface AiRepository {
    suspend fun getRecommendations(history: UserShoppingHistory): AiSearchQuery
}
