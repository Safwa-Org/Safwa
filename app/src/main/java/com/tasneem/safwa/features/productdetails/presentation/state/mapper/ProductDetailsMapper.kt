package com.tasneem.safwa.features.productdetails.presentation.state.mapper

import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails

data class ProductDetailsUiModel(
    val id: String,
    val title: String,
    val vendor: String,
    val description: String,
    val priceFormatted: String,
    val imageUrls: List<String>,
    val imageLabels: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val sizes: List<String>,
)

fun ProductDetails.toUiModel() = ProductDetailsUiModel(
    id = id,
    title = title,
    vendor = vendor,
    description = description,
    priceFormatted = "${priceRange.min.currency} ${priceRange.min.amount}",
    imageUrls = images.map { it.url },
    imageLabels = images.map { it.altText },
    rating = 4.9f,
    reviewCount = 286,
    sizes = variants
        .flatMap { it.selectedOptions }
        .filter { it.name.equals("Size", ignoreCase = true) }
        .map { it.value }
        .distinct()
        .ifEmpty { variants.map { it.title }.distinct() }
)