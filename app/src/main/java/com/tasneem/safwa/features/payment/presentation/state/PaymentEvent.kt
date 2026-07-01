package com.tasneem.safwa.features.payment.presentation.state

sealed interface PaymentEvent {
    data object BackClicked : PaymentEvent
    data class MethodSelected(val method: PaymentMethodType) : PaymentEvent
    data class CardSelected(val cardId: String) : PaymentEvent
    data class ToggleAddCardDialog(val show: Boolean) : PaymentEvent
    data class SaveNewCard(
        val number: String,
        val firstName: String,
        val lastName: String,
        val month: String,
        val year: String,
        val verificationValue: String
    ) : PaymentEvent
    data object ContinueClicked : PaymentEvent
}
