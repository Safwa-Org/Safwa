package com.tasneem.safwa.features.checkout.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class CheckoutState(
    val isLoading: Boolean = false,
    val deliveryAddress: AddressUiModel? = null,
    val paymentMethods: List<PaymentMethodUiModel> = emptyList(),
    val subtotal: String = "",
    val shippingFee: String = "",
    val isShippingFree: Boolean = false,
    val vatAmount: String = "",
    val totalAmount: String = ""
)

data class AddressUiModel(
    val tag: String,
    val recipientName: String,
    val detailedAddress: String
)

data class PaymentMethodUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val isSelected: Boolean
)