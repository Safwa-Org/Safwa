package com.tasneem.safwa.features.productdetails.domain.model

data class ProductDetails(
    val id: String,
    val title: String,
    val handle: String,
    val vendor: String,
    val description: String,
    val descriptionHtml: String,
    val productType: String,
    val priceRange: PriceRange,
    val images: List<ProductImage>,
    val variants: List<ProductVariant>,
)

data class PriceRange(
    val min: Price,
    val max: Price,
)

data class Price(
    val amount: String,
    val currency: String,
)

data class ProductImage(
    val id: String,
    val url: String,
    val altText: String,
    val width: Int?,
    val height: Int?,
)

data class ProductVariant(
    val id: String,
    val title: String,
    val availableForSale: Boolean,
    val selectedOptions: List<SelectedOption>,
    val price: Price,
    val quantityAvailable: Int?,
)

data class SelectedOption(
    val name: String,
    val value: String,
)