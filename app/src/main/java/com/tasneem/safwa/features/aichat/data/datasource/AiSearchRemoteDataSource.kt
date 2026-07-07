package com.tasneem.safwa.features.aichat.data.datasource

import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery

interface AiSearchRemoteDataSource {
    suspend fun translateQuery(userInput: String): SmartSearchQuery
}
