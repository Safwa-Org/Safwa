package com.tasneem.safwa.features.search.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    operator fun invoke(query: String, first: Int = 20): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            val products = repository.searchProducts(query, first)
            emit(Resource.Success(products))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to search products"))
        }
    }
}
