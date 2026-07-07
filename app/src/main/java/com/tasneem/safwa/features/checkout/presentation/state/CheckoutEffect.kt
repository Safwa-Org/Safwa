package com.tasneem.safwa.features.checkout.presentation.state

sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToSavedAddresses : CheckoutEffect
    data class NavigateToOrderConfirmed(
        val orderId: String,
        val totalAmount: String
    ) : CheckoutEffect
}
