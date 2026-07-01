package com.tasneem.network.dto

data class ProductDetailsDto(
    val id: String,
    val title: String,
    val handle: String,
    val description: String?,
    val descriptionHtml: String?,
    val vendor: String,
    val productType: String,
    val collections: CollectionConnection,
    val priceRange: ProductPriceRange,
    val images: ImageConnection,
    val variants: ProductVariantConnection
)

data class CollectionConnection(
    val edges: List<CollectionEdge>
)

data class CollectionEdge(
    val node: CollectionNode
)

data class CollectionNode(
    val id: String,
    val title: String,
    val handle: String
)

data class ProductPriceRange(
    val minVariantPrice: Money,
    val maxVariantPrice: Money
)

data class Money(
    val amount: String,
    val currencyCode: String
)

data class ImageConnection(
    val edges: List<ImageEdge>
)

data class ImageEdge(
    val node: ImageNode
)

data class ImageNode(
    val id: String,
    val url: String,
    val altText: String?,
    val width: Int?,
    val height: Int?
)

data class ProductVariantConnection(
    val edges: List<ProductVariantEdge>
)

data class ProductVariantEdge(
    val node: ProductVariantNode
)

data class ProductVariantNode(
    val id: String,
    val title: String,
    val availableForSale: Boolean,
    val selectedOptions: List<SelectedOption>,
    val price: Money,
    val quantityAvailable: Int?
)

data class SelectedOption(
    val name: String,
    val value: String
)