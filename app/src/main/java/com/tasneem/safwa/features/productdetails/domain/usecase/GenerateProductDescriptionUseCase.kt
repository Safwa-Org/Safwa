package com.tasneem.safwa.features.productdetails.domain.usecase

import com.tasneem.safwa.features.productdetails.domain.repository.ProductDescriptionAiRepository
import javax.inject.Inject

class GenerateProductDescriptionUseCase @Inject constructor(
    private val repository: ProductDescriptionAiRepository
) {
    suspend operator fun invoke(
        title: String,
        vendor: String,
        productType: String,
        price: String,
        currency: String,
        languageCode: String,
    ): String? = repository.generateDescription(
        title = title,
        vendor = vendor,
        productType = productType,
        price = price,
        currency = currency,
        languageCode = languageCode,
    )
}