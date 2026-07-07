package com.tasneem.safwa.features.checkout.presentation.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class CheckoutState(
    val status: CheckoutStatus = CheckoutStatus.Loading,
    val cartItems: List<CheckoutItemUiModel> = emptyList(),
    val addresses: List<AddressUiModel> = emptyList(),
    val selectedAddressId: String? = null,
    val paymentMethod: PaymentMethodUiModel? = null,
    val summary: OrderSummaryUiModel = OrderSummaryUiModel(),
    val showAddressPicker: Boolean = false
) {
    val selectedAddress: AddressUiModel?
        get() = addresses.firstOrNull { it.id == selectedAddressId }
}

@Immutable
sealed interface CheckoutStatus {
    data object Loading : CheckoutStatus
    data object Ready : CheckoutStatus
    data class PlacingOrder(val step: PlaceOrderStep) : CheckoutStatus
    data class Completed(val orderName: String) : CheckoutStatus

    data class Failure(
        val message: String? = null,
        @StringRes val messageRes: Int? = null,
        val retry: RetryAction = RetryAction.NONE
    ) : CheckoutStatus
}

enum class PlaceOrderStep { CREATING_ORDER, PROCESSING_PAYMENT, CONFIRMING_PAYMENT }

enum class RetryAction { NONE, RELOAD, PLACE_ORDER, MARK_PAID }

@Immutable
data class AddressUiModel(
    val id: String,
    val tag: String,
    val recipientName: String,
    val detailedAddress: String
)

@Immutable
data class PaymentMethodUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val isSelected: Boolean
)

@Immutable
data class CheckoutItemUiModel(
    val id: String,
    val title: String,
    val variant: String,
    val quantity: Int,
    val imageUrl: String,
    val lineTotal: String
)

@Immutable
data class OrderSummaryUiModel(
    val subtotal: String = "",
    val discount: String? = null,
    val discountCode: String? = null,
    val shippingFee: String? = null,
    val isShippingFree: Boolean = false,
    val vatAmount: String? = null,
    val totalAmount: String = ""
)
