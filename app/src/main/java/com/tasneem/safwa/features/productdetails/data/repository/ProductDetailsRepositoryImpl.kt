package com.tasneem.safwa.features.productdetails.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.productdetails.data.mapper.toDomain
import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDetailsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductDetailsRepositoryImpl @Inject constructor(
    private val remote: ProductRemoteDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) : ProductDetailsRepository {

    override suspend fun getProductDetailsByHandle(handle: String): ProductDetails {
        return safeCall { 
            val languageCode = sessionPreferencesRepository.appPreferences.first().languageCode
            remote.getProductDetailsByHandle(handle, languageCode).toDomain() 
        }
    }
}
