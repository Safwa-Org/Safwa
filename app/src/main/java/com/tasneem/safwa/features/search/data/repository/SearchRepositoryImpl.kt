package com.tasneem.safwa.features.search.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : SearchRepository {

    override suspend fun searchProducts(query: String, first: Int): List<Product> {
        val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
        val dtos = remoteDataSource.searchProducts(query, first, languageCode)
        return dtos.map { it.toDomainModel() }
    }
}
