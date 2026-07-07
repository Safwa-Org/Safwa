package com.tasneem.safwa.features.aichat.domain.usecase

import android.util.Log
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import com.tasneem.safwa.features.aichat.domain.model.SmartSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AiSearchProductsUseCase @Inject constructor(
    private val translateQueryUseCase: TranslateQueryUseCase,
    private val searchRepository: SearchRepository
) {
    operator fun invoke(userInput: String): Flow<Resource<Pair<List<Product>, SmartSearchQuery>>> = flow {
        emit(Resource.Loading)
        try {
            val smartSearchQuery = translateQueryUseCase(userInput)
            Log.d("AiSearch", "Generated Shopify Query: ${smartSearchQuery.query}")
            val products = searchRepository.searchProducts(smartSearchQuery.query, smartSearchQuery.first)
            emit(Resource.Success(Pair(products, smartSearchQuery)))
        } catch (e: Exception) {
            Log.e("AiSearch", "Error searching products", e)
            emit(Resource.Error(e.localizedMessage ?: "Failed to perform AI smart search"))
        }
    }
}
