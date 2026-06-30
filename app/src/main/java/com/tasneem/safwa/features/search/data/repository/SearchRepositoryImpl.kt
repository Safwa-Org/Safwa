package com.tasneem.safwa.features.search.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : SearchRepository {

    override suspend fun searchProducts(query: String, first: Int): List<Product> {
        val dtos = remoteDataSource.searchProducts(query, first)
        return dtos.map { it.toDomainModel() }
    }
}
