package com.tasneem.safwa.features.checkout.domain.model

import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address

data class CheckoutData(
    val cart: Cart,
    val addresses: List<Address>,
    val savedCards: List<SavedCard>,
    val customerEmail: String?
)

data class OrderSummary(
    val subtotal: Double,
    val discount: Double,
    val appliedDiscountCode: String?,
    val shipping: Double?,
    val tax: Double?,
    val total: Double,
    val currency: String
)

data class DraftOrder(
    val id: String,
    val subtotal: Double?,
    val tax: Double?,
    val shipping: Double?,
    val total: Double,
    val currency: String
)

data class CompletedOrder(
    val draftOrderId: String,
    val orderId: String,
    val orderName: String
)

data class DraftOrderRequest(
    val email: String?,
    val lineItems: List<DraftOrderLineItem>,
    val shippingAddress: Address,
    val discountAmount: Double?,
    val discountCode: String?,
    val shippingLineTitle: String?,
    val shippingLineAmount: Double?,
    val note: String?
)

data class DraftOrderLineItem(
    val variantId: String,
    val quantity: Int
)

sealed interface CheckoutValidationError {
    data object EmptyCart : CheckoutValidationError
    data object MissingAddress : CheckoutValidationError
    data object MissingPaymentMethod : CheckoutValidationError
}

data class CheckoutPaymentSelection(
    val method: PaymentMethodType,
    val cardId: String? = null
)
