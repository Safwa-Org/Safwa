package com.tasneem.network.dto.checkout

data class DraftOrderInputDto(
    val lineItems: List<DraftOrderLineItemInputDto>,
    val email: String? = null,
    val shippingAddress: MailingAddressInputDto? = null,
    val billingAddress: MailingAddressInputDto? = null,
    val appliedDiscount: DraftOrderAppliedDiscountInputDto? = null,
    val shippingLine: ShippingLineInputDto? = null,
    val note: String? = null,
    val tags: List<String>? = null
)

data class DraftOrderLineItemInputDto(
    val variantId: String,
    val quantity: Int
)

data class MailingAddressInputDto(
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val province: String? = null,
    val country: String? = null,
    val zip: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
)

data class DraftOrderAppliedDiscountInputDto(
    val title: String? = null,
    val description: String? = null,
    val value: Double,
    val valueType: String
)

data class ShippingLineInputDto(
    val title: String,
    val price: String
)

data class MoneyBagDto(val shopMoney: MoneyDto?)
data class MoneyDto(val amount: String?, val currencyCode: String?)

data class DraftOrderCreateDataDto(
    val draftOrderCreate: DraftOrderCreatePayloadDto?
)

data class DraftOrderCreatePayloadDto(
    val draftOrder: DraftOrderDto?,
    val userErrors: List<UserErrorDto> = emptyList()
)

data class DraftOrderDto(
    val id: String,
    val invoiceUrl: String? = null,
    val subtotalPriceSet: MoneyBagDto? = null,
    val totalTaxSet: MoneyBagDto? = null,
    val totalShippingPriceSet: MoneyBagDto? = null,
    val totalPriceSet: MoneyBagDto? = null
)

// draftOrderComplete

data class DraftOrderCompleteDataDto(
    val draftOrderComplete: DraftOrderCompletePayloadDto?
)

data class DraftOrderCompletePayloadDto(
    val draftOrder: CompletedDraftOrderDto?,
    val userErrors: List<UserErrorDto> = emptyList()
)

data class CompletedDraftOrderDto(
    val id: String,
    val order: OrderRefDto?
)

data class OrderRefDto(
    val id: String,
    val name: String? = null
)

// orderMarkAsPaid

data class OrderMarkAsPaidDataDto(
    val orderMarkAsPaid: OrderMarkAsPaidPayloadDto?
)

data class OrderMarkAsPaidPayloadDto(
    val order: OrderRefDto? = null,
    val userErrors: List<UserErrorDto> = emptyList()
)

// orderCancel

data class OrderCancelDataDto(
    val orderCancel: OrderCancelPayloadDto?
)

data class OrderCancelPayloadDto(
    val job: JobDto? = null,
    val orderCancelUserErrors: List<UserErrorDto> = emptyList(),
    val userErrors: List<UserErrorDto> = emptyList()
)

data class JobDto(
    val id: String? = null,
    val done: Boolean? = null
)
