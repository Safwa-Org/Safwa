package com.tasneem.safwa.features.checkout.presentation.state

sealed interface CheckoutEffect {
    object NavigateBack : CheckoutEffect
    object NavigateToSavedAddresses : CheckoutEffect
    object NavigateToPaymentConfirmation : CheckoutEffect
    data class ShowError(val message: String) : CheckoutEffect
}