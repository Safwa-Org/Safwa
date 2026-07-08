package com.tasneem.safwa.features.category.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : CategoryRepository {
    override suspend fun getCategories(first: Int): List<Category> {
        return safeCall {
            remoteDataSource.getCategories(first).map { dto ->
                Category(
                    id = dto.id,
                    title = dto.title,
                    handle = dto.handle,
                    imageUrl = dto.imageUrl
                )
            }
        }
    }

    override suspend fun getProductsByCategory(category: String, first: Int): List<Product> {
        return safeCall {
            val handle = category.lowercase().replace(" ", "-")
            remoteDataSource.getCollectionProducts(handle, first).map { it.toDomainModel() }
        }
    }
}

