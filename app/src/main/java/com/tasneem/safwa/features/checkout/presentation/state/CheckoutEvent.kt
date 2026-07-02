package com.tasneem.safwa.features.checkout.presentation.state

sealed interface CheckoutEvent {
    object OnBackClick : CheckoutEvent
    object OnChangeAddressClick : CheckoutEvent
    data class OnPaymentMethodSelected(val methodId: String) : CheckoutEvent
    object OnPlaceOrderClick : CheckoutEvent
}