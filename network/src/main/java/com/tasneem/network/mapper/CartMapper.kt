package com.tasneem.network.mapper

import com.tasneem.network.dto.CartDto
import com.tasneem.network.dto.CartLineDto
import com.tasneem.network.dto.DiscountCodeDto
import com.tasneem.safwa.network.GetCartQuery
import javax.inject.Inject

class CartMapper @Inject constructor() : Mapper<GetCartQuery.Cart, CartDto> {
    override fun map(input: GetCartQuery.Cart): CartDto {
        val deliveryOption = input.deliveryGroups.edges.firstOrNull()
            ?.node?.deliveryOptions?.firstOrNull()
        val shippingAmount = deliveryOption?.estimatedCost?.amount?.toString()

        return CartDto(
            id = input.id,
            checkoutUrl = input.checkoutUrl.toString(),
            subtotalAmount = input.cost.subtotalAmount.amount.toString(),
            totalAmount = input.cost.totalAmount.amount.toString(),
            currency = input.cost.totalAmount.currencyCode.name,
            shippingAmount = shippingAmount,
            shippingTitle = deliveryOption?.title,
            taxAmount = input.cost.totalTaxAmount?.amount?.toString(),
            discountCodes = input.discountCodes.map { discount ->
                DiscountCodeDto(
                    code = discount.code,
                    applicable = discount.applicable
                )
            },
            lines = input.lines.edges.mapNotNull { edge ->
                val node = edge.node
                val merchandise = node.merchandise.onProductVariant
                if (merchandise != null) {
                    CartLineDto(
                        id = node.id,
                        variantId = merchandise.id,
                        productId = "",
                        vendor = "",
                        productTitle = merchandise.product.title,
                        variantTitle = merchandise.title,
                        productHandle = "",
                        imageUrl = merchandise.product.featuredImage?.url?.toString() ?: "",
                        price = merchandise.priceV2.amount.toString(),
                        totalLinePrice = node.cost.totalAmount.amount.toString(),
                        currency = merchandise.priceV2.currencyCode.name,
                        quantity = node.quantity
                    )
                } else {
                    null
                }
            }
        )
    }
}
