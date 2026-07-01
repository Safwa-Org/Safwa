package com.tasneem.safwa.features.category.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import com.tasneem.safwa.features.core.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(category: String, first: Int = 20): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading)
            val products = repository.getProductsByCategory(category, first)
            emit(Resource.Success(products))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}

