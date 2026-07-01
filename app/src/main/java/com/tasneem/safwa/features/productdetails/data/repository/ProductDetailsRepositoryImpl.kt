package com.tasneem.safwa.features.productdetails.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.safwa.core.data.safeCall
import com.tasneem.safwa.features.productdetails.data.mapper.toDomain
import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDetailsRepository
import javax.inject.Inject

class ProductDetailsRepositoryImpl @Inject constructor(
    private val remote: ProductRemoteDataSource
) : ProductDetailsRepository {

    override suspend fun getProductDetailsByHandle(handle: String): ProductDetails {
        return safeCall { remote.getProductDetailsByHandle(handle).toDomain() }
    }
}
