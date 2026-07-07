package com.tasneem.safwa.features.home.data.repository

import com.tasneem.safwa.features.home.data.datasource.AiRemoteDataSource
import com.tasneem.safwa.features.home.domain.repository.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource
) : AiRepository {
    override suspend fun getRecommendedProductIds(prompt: String): List<String> {
        return aiRemoteDataSource.getRecommendations(prompt)
    }
}
