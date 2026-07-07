package com.tasneem.safwa.features.payment.presentation.state

sealed interface PaymentEffect {
    data object NavigateBack : PaymentEffect
    data object NavigateToHome : PaymentEffect
    data class NavigateToOrderConfirmed(val orderId: String? = null, val totalAmount: String? = null) : PaymentEffect
    data object NavigateToOrderFailed : PaymentEffect
    data class ShowSnackBar(@androidx.annotation.StringRes val messageRes: Int) : PaymentEffect
    data class LaunchShopifyCheckout(val url: String) : PaymentEffect

    data class NavigateToCheckout(val methodId: String) : PaymentEffect
}
