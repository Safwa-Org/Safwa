package com.tasneem.safwa.features.home.data.repository

import com.tasneem.safwa.features.home.data.datasource.AiRemoteDataSource
import com.tasneem.safwa.features.home.domain.model.AiSearchQuery
import com.tasneem.safwa.features.home.domain.model.UserShoppingHistory
import com.tasneem.safwa.features.home.domain.repository.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource
) : AiRepository {
    override suspend fun getRecommendations(history: UserShoppingHistory): AiSearchQuery {
        return aiRemoteDataSource.getRecommendations(history)
    }
}
