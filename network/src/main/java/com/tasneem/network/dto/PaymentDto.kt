package com.tasneem.network.dto

data class PaymentRequestDto(
    val orderId: String,
    val paymentMethodId: String? = null,
    val amount: Double? = null
)

data class PaymentResponseDto(
    val success: Boolean,
    val transactionId: String? = null,
    val errorMessage: String? = null
)
