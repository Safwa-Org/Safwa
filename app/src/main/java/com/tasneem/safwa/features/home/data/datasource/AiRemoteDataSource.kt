package com.tasneem.safwa.features.home.data.datasource

import com.tasneem.safwa.features.home.domain.model.AiSearchQuery
import com.tasneem.safwa.features.home.domain.model.UserShoppingHistory

interface AiRemoteDataSource {
    suspend fun getRecommendations(history: UserShoppingHistory): AiSearchQuery
}
