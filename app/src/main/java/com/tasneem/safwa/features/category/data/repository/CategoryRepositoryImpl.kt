package com.tasneem.safwa.features.category.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : CategoryRepository {
    override suspend fun getCategories(first: Int): List<Category> {
        val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
        return remoteDataSource.getCategories(first, languageCode).map { dto ->
            Category(
                id = dto.id,
                title = dto.title,
                handle = dto.handle,
                imageUrl = dto.imageUrl
            )
        }
    }

    override suspend fun getProductsByCategory(category: String, first: Int): List<Product> {
        val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
        val handle = category.lowercase().replace(" ", "-")
        val dtos = remoteDataSource.getCollectionProducts(handle, first, languageCode)
        return dtos.map { it.toDomainModel() }
    }
}

