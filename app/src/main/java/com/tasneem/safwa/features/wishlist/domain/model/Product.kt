package com.tasneem.safwa.features.wishlist.domain.model

data class Product(
    val id: String = "",
    val title: String = "",
    val handle: String = "",
    val description: String = "",
    val vendor: String = "",
    val productType: String = "",
    val price: String = "",
    val currency: String = "",
    val imageUrl: List<String> = emptyList(),
    val imageAltText: List<String> = emptyList(),
)