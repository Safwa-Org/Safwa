package com.tasneem.safwa.features.home.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLatestProductsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(first: Int = 4): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading)
            val products = repository.getProducts(first = first, sortKey = "CREATED_AT", reverse = true)
            emit(Resource.Success(products))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
