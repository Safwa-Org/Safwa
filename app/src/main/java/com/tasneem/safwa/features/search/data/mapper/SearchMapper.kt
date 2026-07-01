package com.tasneem.safwa.features.search.data.mapper

import com.tasneem.network.dto.ProductDto
import com.tasneem.safwa.features.core.domain.model.Product

fun ProductDto.toDomainModel(): Product {
    return Product(
        id = id,
        title = title,
        handle = handle,
        description = description ?: "",
        vendor = vendor,
        productType = productType,
        price = price,
        currency = currency,
        imageUrl = imageUrls.filterNotNull(),
        imageAltText = imageAlts.filterNotNull()
    )
}
