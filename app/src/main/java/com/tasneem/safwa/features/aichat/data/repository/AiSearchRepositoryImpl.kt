package com.tasneem.safwa.features.aichat.data.repository

import com.tasneem.safwa.features.aichat.data.datasource.AiSearchRemoteDataSource
import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery
import com.tasneem.safwa.features.aichat.domain.repository.AiSearchRepository
import javax.inject.Inject

class AiSearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiSearchRemoteDataSource
) : AiSearchRepository {

    override suspend fun translateToShopifyQuery(userInput: String): SmartSearchQuery {
        return remoteDataSource.translateQuery(userInput)
    }
}
