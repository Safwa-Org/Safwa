package com.tasneem.safwa.features.aichat.domain.usecase

import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery
import com.tasneem.safwa.features.aichat.domain.repository.AiSearchRepository
import javax.inject.Inject

class TranslateQueryUseCase @Inject constructor(
    private val repository: AiSearchRepository
) {
    suspend operator fun invoke(userInput: String): SmartSearchQuery {
        return repository.translateToShopifyQuery(userInput)
    }
}
