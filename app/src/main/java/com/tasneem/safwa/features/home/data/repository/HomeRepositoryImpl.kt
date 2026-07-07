package com.tasneem.safwa.features.home.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : HomeRepository {
    override suspend fun getProducts(first: Int, after: String?, sortKey: String?): List<Product> {
        return safeCall {
            remoteDataSource.getProducts(first, after, sortKey).map { it.toDomainModel() }
        }
    }
}
