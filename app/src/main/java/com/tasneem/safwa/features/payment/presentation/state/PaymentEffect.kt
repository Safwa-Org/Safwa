package com.tasneem.safwa.features.payment.presentation.state

sealed interface PaymentEffect {
    data object NavigateBack : PaymentEffect
    data object NavigateToHome : PaymentEffect
    data class ShowSnackBar(@androidx.annotation.StringRes val messageRes: Int) : PaymentEffect
}
