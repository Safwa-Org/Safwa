package com.tasneem.safwa.features.payment.presentation.state

enum class PaymentMethodType {
    CASH_ON_DELIVERY,
    VISA
}

data class SavedCard(
    val id: String,
    val type: String,
    val last4: String,
    val cardholderName: String,
    val expiryDate: String,
    val isDefault: Boolean
)



data class PaymentState(
    val isLoading: Boolean = false,
    val savedCards: List<SavedCard> = emptyList(),
    val selectedMethod: PaymentMethodType? = null,
    val selectedCardId: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showAddCardDialog: Boolean = false
)
