package com.tasneem.safwa.features.payment.presentation.state

import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType

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
    data object PayPalClicked : PaymentEvent
    /** User confirmed payment inside the in-app PayPal dialog. */
    data object PayPalDialogConfirmed : PaymentEvent
    /** User dismissed/cancelled the in-app PayPal dialog. */
    data object PayPalDialogDismissed : PaymentEvent
}
