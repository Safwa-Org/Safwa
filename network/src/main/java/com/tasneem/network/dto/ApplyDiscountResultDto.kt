package com.tasneem.network.dto

data class ApplyDiscountResultDto(
    val discountCodes: List<DiscountCodeDto>,
    val subtotalAmount: String,
    val totalAmount: String,
    val currency: String
)
