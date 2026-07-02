package com.tasneem.network.dto

data class CartDto(
    val id: String,
    val checkoutUrl: String,
    val subtotalAmount: String,
    val totalAmount: String,
    val currency: String,
    val lines: List<CartLineDto>
)

data class CartLineDto(
    val id: String,
    val variantId: String,
    val productId: String,
    val vendor: String,
    val productTitle: String,
    val variantTitle: String,
    val productHandle: String,
    val imageUrl: String,
    val price: String,
    val totalLinePrice: String,
    val currency: String,
    val quantity: Int
)
