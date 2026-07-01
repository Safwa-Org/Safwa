package com.tasneem.safwa.features.core.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(first: Int = 100): Flow<Resource<List<Category>>> = flow {
        try {
            emit(Resource.Loading)
            val categories = repository.getCategories(first)
            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}