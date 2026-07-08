package com.tasneem.safwa.features.productdetails.data.datasource

interface ProductDescriptionAiRemoteDataSource {
    suspend fun generateDescription(
        title: String,
        vendor: String,
        productType: String,
        price: String,
        currency: String,
        languageCode: String,
    ): String?
}