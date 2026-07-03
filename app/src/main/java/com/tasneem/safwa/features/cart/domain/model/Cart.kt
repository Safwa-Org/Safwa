package com.tasneem.safwa.features.cart.domain.model

data class Cart(
    val id: String,
    val checkoutUrl: String,
    val subtotalAmount: String,
    val totalAmount: String,
    val currency: String,
    val shippingAmount: String?,
    val discountCodes: List<DiscountCode> = emptyList(),
    val lines: List<CartLine>
)

data class CartLine(
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

data class DiscountCode(
    val code: String,
    val applicable: Boolean
)
