package com.tasneem.safwa.features.productdetails.data.repository

import com.tasneem.safwa.features.productdetails.data.datasource.ProductDescriptionAiRemoteDataSource
import com.tasneem.safwa.features.productdetails.domain.repository.ProductDescriptionAiRepository
import javax.inject.Inject

class ProductDescriptionAiRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductDescriptionAiRemoteDataSource
) : ProductDescriptionAiRepository {

    override suspend fun generateDescription(
        title: String,
        vendor: String,
        productType: String,
        price: String,
        currency: String,
        languageCode: String,
    ): String? = remoteDataSource.generateDescription(
        title = title,
        vendor = vendor,
        productType = productType,
        price = price,
        currency = currency,
        languageCode = languageCode,
    )
}