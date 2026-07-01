package com.tasneem.safwa.features.productdetails.domain.mapper

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails

fun ProductDetails.toProduct() = Product(
    id = id,
    title = title,
    handle = handle,
    description = description,
    vendor = vendor,
    productType = productType,
    price = priceRange.min.amount,
    currency = priceRange.min.currency,
    imageUrl = images.map { it.url },
    imageAltText = images.map { it.altText },
)
