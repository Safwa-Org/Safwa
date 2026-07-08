package com.tasneem.safwa.features.brand.data.repository

import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.data.mapper.toDomainModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val remoteDataSource: BrandRemoteDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : BrandRepository {

    override suspend fun getBrands(): List<Brand> {
        return safeCall {
            val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
            val brands = remoteDataSource.getProductVendors(languageCode = languageCode)
                .filter { it.isNotBlank() }
                .distinct()
                .sortedBy { it.lowercase() }
                .map { vendor -> Brand(name = vendor, imageUrl = null) }
            brands
        } ?: emptyList()
    }

    override suspend fun getProductsByBrand(brandName: String): List<Product> {
        return safeCall {
            val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
            remoteDataSource.getProductsForVendor(brandName, languageCode = languageCode).map { it.toDomainModel() }
        }
    }
}
