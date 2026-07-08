package com.tasneem.safwa.features.productdetails.domain.repository

interface ProductDescriptionAiRepository {
    suspend fun generateDescription(
        title: String,
        vendor: String,
        productType: String,
        price: String,
        currency: String,
        languageCode: String,
    ): String?
}