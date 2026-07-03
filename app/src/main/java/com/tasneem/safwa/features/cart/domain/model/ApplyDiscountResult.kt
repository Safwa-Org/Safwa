package com.tasneem.safwa.features.cart.domain.model

data class ApplyDiscountResult(
    val discountCodes: List<DiscountCode>,
    val subtotalAmount: String,
    val totalAmount: String,
    val currency: String
)
