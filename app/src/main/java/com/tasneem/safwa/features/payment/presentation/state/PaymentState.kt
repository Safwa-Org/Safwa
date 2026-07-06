package com.tasneem.safwa.features.payment.presentation.state

import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.model.SavedCard

data class PaymentState(
    val isLoading: Boolean = false,
    val savedCards: List<SavedCard> = emptyList(),
    val selectedMethod: PaymentMethodType? = null,
    val selectedCardId: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showAddCardDialog: Boolean = false,
    val pendingPayPalOrderId: String? = null,
    val checkoutUrl: String? = null
)
