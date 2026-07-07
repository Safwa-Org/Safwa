package com.tasneem.safwa.features.checkout.presentation.mapper

import com.tasneem.safwa.features.cart.domain.model.CartLine
import com.tasneem.safwa.features.checkout.domain.model.CheckoutPaymentSelection
import com.tasneem.safwa.features.checkout.domain.model.OrderSummary
import com.tasneem.safwa.features.checkout.presentation.state.AddressUiModel
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutItemUiModel
import com.tasneem.safwa.features.checkout.presentation.state.OrderSummaryUiModel
import com.tasneem.safwa.features.checkout.presentation.state.PaymentMethodUiModel
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import java.util.Locale


object PaymentMethodIds {
    const val CASH_ON_DELIVERY = "cod"
    const val PAYMOCK = "paymock"
    private const val CARD_PREFIX = "card:"

    fun cardId(card: SavedCard) = "$CARD_PREFIX${card.id}"

    fun toSelection(methodId: String): CheckoutPaymentSelection? = when {
        methodId == CASH_ON_DELIVERY ->
            CheckoutPaymentSelection(PaymentMethodType.CASH_ON_DELIVERY)
        methodId == PAYMOCK ->
            CheckoutPaymentSelection(PaymentMethodType.PAYMOCK)
        methodId.startsWith(CARD_PREFIX) ->
            CheckoutPaymentSelection(PaymentMethodType.VISA, methodId.removePrefix(CARD_PREFIX))
        else -> null
    }
}

fun Address.toUiModel() = AddressUiModel(
    id = id,
    tag = label.ifBlank { "Address" },
    recipientName = recipientName,
    detailedAddress = listOf(street, cityAndZip)
        .filter { it.isNotBlank() }
        .joinToString(", ")
)

fun CartLine.toUiModel() = CheckoutItemUiModel(
    id = id,
    title = productTitle,
    variant = variantTitle,
    quantity = quantity,
    imageUrl = imageUrl,
    lineTotal = formatMoney(currency, totalLinePrice.toDoubleOrNull() ?: 0.0)
)


fun selectedPaymentUiModel(
    methodId: String,
    savedCards: List<SavedCard>
): PaymentMethodUiModel? {
    val selection = PaymentMethodIds.toSelection(methodId) ?: return null
    return when (selection.method) {
        PaymentMethodType.CASH_ON_DELIVERY -> PaymentMethodUiModel(
            id = methodId,
            title = "Cash on Delivery",
            subtitle = "Pay when it arrives",
            isSelected = true
        )
        PaymentMethodType.PAYMOCK -> PaymentMethodUiModel(
            id = methodId,
            title = "PayMock",
            subtitle = "Sandbox test payment",
            isSelected = true
        )
        PaymentMethodType.VISA -> {
            val card = savedCards.firstOrNull { it.id == selection.cardId }
            PaymentMethodUiModel(
                id = methodId,
                title = card?.type ?: "Card",
                subtitle = card?.let { "•••• ${it.last4}" } ?: "Saved card",
                isSelected = true
            )
        }
        else -> null
    }
}

fun OrderSummary.toUiModel() = OrderSummaryUiModel(
    subtotal = formatMoney(currency, subtotal),
    discount = discount.takeIf { it > 0.0 }?.let { "- ${formatMoney(currency, it)}" },
    discountCode = appliedDiscountCode,
    shippingFee = shipping?.let { formatMoney(currency, it) },
    isShippingFree = shipping == 0.0,
    vatAmount = tax?.let { formatMoney(currency, it) },
    totalAmount = formatMoney(currency, total)
)

fun formatMoney(currency: String, amount: Double): String =
    "$currency ${String.format(Locale.US, "%,.2f", amount)}"
