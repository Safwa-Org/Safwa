package com.tasneem.safwa.features.home.domain.usecase

import com.tasneem.safwa.features.home.domain.repository.AiRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.model.AiSearchQuery
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAiRecommendationsUseCase @Inject constructor(
    private val getUserShoppingHistoryUseCase: GetUserShoppingHistoryUseCase,
    private val searchRepository: SearchRepository,
    private val aiRepository: AiRepository
) {
    operator fun invoke(): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading)

            val history = getUserShoppingHistoryUseCase()

            if (history.wishlistItems.isEmpty() && history.cartItems.isEmpty() && history.orderItems.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@flow
            }

            val aiSearchQuery = try {
                aiRepository.getRecommendations(history)
            } catch (e: Exception) {
                AiSearchQuery(query = "", first = 20)
            }

            val recommendedProducts = searchRepository.searchProducts(aiSearchQuery.query, aiSearchQuery.first)

            emit(Resource.Success(recommendedProducts))

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get AI recommendations"))
        }
    }
}
