package com.tasneem.safwa.features.productdetails.presentation.state.mapper

import com.tasneem.safwa.features.wishlist.domain.model.Product

data class ProductUiModel(
    val id: String = "",
    val title: String = "",
    val vendor: String = "",
    val description: String = "",
    val priceFormatted: String = "",
    val imageUrls: List<String> = listOf(),
    val imageLabels: List<String> = listOf(),
    val rating: Float = 4.9f,
    val reviewCount: Int = 286,
    val sizes: List<String> = listOf("30 ml", "50 ml", "100 ml")
)

fun Product.toUiModel(): ProductUiModel {
    return ProductUiModel(
        id = this.id,
        title = this.title,
        vendor = this.vendor,
        description = this.description,
        priceFormatted = "${this.currency} ${this.price}",
        imageUrls = this.imageUrl,
        imageLabels = this.imageAltText,
    )
}