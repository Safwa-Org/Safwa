package com.tasneem.safwa.features.checkout.presentation.state

sealed interface CheckoutEvent {
    data object OnBackClick : CheckoutEvent

    data object OnChangeAddressClick : CheckoutEvent
    data object OnDismissAddressPicker : CheckoutEvent
    data class OnAddressSelected(val addressId: String) : CheckoutEvent
    data object OnManageAddressesClick : CheckoutEvent

    data object OnScreenResumed : CheckoutEvent

    data object OnChangePaymentMethodClick : CheckoutEvent

    data object OnPlaceOrderClick : CheckoutEvent
    data object OnRetry : CheckoutEvent
    data object OnErrorDismiss : CheckoutEvent
}
