package com.tasneem.safwa.features.home.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.core.domain.model.PaginatedData
import com.tasneem.safwa.features.home.domain.repository.HomeRepository
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : HomeRepository {
    override suspend fun getProducts(first: Int, after: String?, sortKey: String?, reverse: Boolean?): List<Product> {
        val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
        return remoteDataSource.getProducts(first, after, sortKey, reverse, languageCode).map { it.toDomainModel() }
    }

    override suspend fun getPaginatedProducts(
        first: Int,
        after: String?,
        sortKey: String?,
        reverse: Boolean?
    ): PaginatedData<Product> {
        val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
        val result = remoteDataSource.getPaginatedProducts(first, after, sortKey, reverse, languageCode)
        return PaginatedData(
            items = result.items.map { it.toDomainModel() },
            endCursor = result.endCursor,
            hasNextPage = result.hasNextPage
        )
    }
}
