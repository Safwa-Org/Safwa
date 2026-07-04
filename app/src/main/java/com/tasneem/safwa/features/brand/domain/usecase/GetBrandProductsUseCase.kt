package com.tasneem.safwa.features.brand.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import com.tasneem.safwa.features.core.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBrandProductsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    operator fun invoke(brandName: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(repository.getProductsByBrand(brandName)))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage, throwable = e))
        }
    }
}
