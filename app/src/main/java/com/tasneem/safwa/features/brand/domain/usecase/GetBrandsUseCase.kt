package com.tasneem.safwa.features.brand.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    operator fun invoke(): Flow<Resource<List<Brand>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(repository.getBrands()))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage, throwable = e))
        }
    }
}
