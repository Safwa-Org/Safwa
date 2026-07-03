package com.tasneem.safwa.features.cart.data.mapper

import com.tasneem.network.dto.ApplyDiscountResultDto
import com.tasneem.network.dto.CartDto
import com.tasneem.safwa.features.cart.domain.model.ApplyDiscountResult
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.model.CartLine
import com.tasneem.safwa.features.cart.domain.model.DiscountCode

fun CartDto.toDomain(): Cart {
    return Cart(
        id = this.id,
        checkoutUrl = this.checkoutUrl,
        subtotalAmount = this.subtotalAmount,
        totalAmount = this.totalAmount,
        currency = this.currency,
        shippingAmount = this.shippingAmount,
        discountCodes = this.discountCodes.map { discount ->
            DiscountCode(
                code = discount.code,
                applicable = discount.applicable
            )
        },
        lines = this.lines.map { line ->
            CartLine(
                id = line.id,
                variantId = line.variantId,
                productId = line.productId,
                vendor = line.vendor,
                productTitle = line.productTitle,
                variantTitle = line.variantTitle,
                productHandle = line.productHandle,
                imageUrl = line.imageUrl,
                price = line.price,
                totalLinePrice = line.totalLinePrice,
                currency = line.currency,
                quantity = line.quantity
            )
        }
    )
}

fun ApplyDiscountResultDto.toDomain(): ApplyDiscountResult {
    return ApplyDiscountResult(
        discountCodes = this.discountCodes.map { discount ->
            DiscountCode(
                code = discount.code,
                applicable = discount.applicable
            )
        },
        subtotalAmount = this.subtotalAmount,
        totalAmount = this.totalAmount,
        currency = this.currency
    )
}
