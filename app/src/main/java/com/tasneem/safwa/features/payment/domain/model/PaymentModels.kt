package com.tasneem.safwa.features.payment.domain.model

enum class PaymentMethodType {
    CASH_ON_DELIVERY,
    VISA,
    PAYPAL,
    SHOPIFY
}

data class SavedCard(
    val id: String,
    val type: String,
    val last4: String,
    val cardholderName: String,
    val expiryDate: String,
    val isDefault: Boolean
)

data class PaymentDetails(
    val method: PaymentMethodType,
    val cardId: String? = null
)

data class CardDetails(
    val number: String,
    val firstName: String,
    val lastName: String,
    val month: String,
    val year: String,
    val verificationValue: String
)
