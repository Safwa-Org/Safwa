package com.tasneem.safwa.features.checkout.data.mapper

import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderAppliedDiscountInputDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderInputDto
import com.tasneem.network.dto.checkout.DraftOrderLineItemInputDto
import com.tasneem.network.dto.checkout.MailingAddressInputDto
import com.tasneem.network.dto.checkout.MoneyBagDto
import com.tasneem.network.dto.checkout.ShippingLineInputDto
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import java.util.Locale

fun DraftOrderRequest.toDto(): DraftOrderInputDto = DraftOrderInputDto(
    lineItems = lineItems.map { DraftOrderLineItemInputDto(it.variantId, it.quantity) },
    email = email?.takeIf { it.isNotBlank() },
    shippingAddress = shippingAddress.toMailingAddressInput(),
    billingAddress = shippingAddress.toMailingAddressInput(),
    appliedDiscount = discountAmount?.let { amount ->
        DraftOrderAppliedDiscountInputDto(
            title = discountCode,
            description = discountCode?.let { "Cart coupon: $it" },
            value = amount,
            valueType = "FIXED_AMOUNT"
        )
    },
    shippingLine = shippingLineAmount?.let { amount ->
        ShippingLineInputDto(
            title = shippingLineTitle ?: "Standard Shipping",
            price = String.format(Locale.US, "%.2f", amount)
        )
    },
    note = note,
    tags = listOf("safwa-android")
)

private fun Address.toMailingAddressInput(): MailingAddressInputDto = MailingAddressInputDto(
    address1 = street.takeIf { it.isNotBlank() },
    address2 = apartment.takeIf { it.isNotBlank() },
    city = city.takeIf { it.isNotBlank() },
    province = province.takeIf { it.isNotBlank() },
    country = countryName.takeIf { it.isNotBlank() },
    zip = zip.takeIf { it.isNotBlank() },
    firstName = firstName.takeIf { it.isNotBlank() },
    lastName = lastName.takeIf { it.isNotBlank() },
    phone = phone.takeIf { it.isNotBlank() }
)

fun DraftOrderDto.toDomain(): DraftOrder = DraftOrder(
    id = id,
    subtotal = subtotalPriceSet.amountOrNull(),
    tax = totalTaxSet.amountOrNull(),
    shipping = totalShippingPriceSet.amountOrNull(),
    total = totalPriceSet.amountOrNull() ?: 0.0,
    currency = totalPriceSet?.shopMoney?.currencyCode.orEmpty()
)

fun CompletedDraftOrderDto.toDomain(): CompletedOrder? {
    val completedOrder = order ?: return null
    return CompletedOrder(
        draftOrderId = id,
        orderId = completedOrder.id,
        orderName = completedOrder.name ?: completedOrder.id.substringAfterLast("/")
    )
}

private fun MoneyBagDto?.amountOrNull(): Double? =
    this?.shopMoney?.amount?.toDoubleOrNull()
