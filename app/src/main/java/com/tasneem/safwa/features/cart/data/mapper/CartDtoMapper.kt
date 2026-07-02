package com.tasneem.safwa.features.cart.data.mapper

import com.tasneem.network.dto.CartDto
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.model.CartLine

fun CartDto.toDomain(): Cart {
    return Cart(
        id = this.id,
        checkoutUrl = this.checkoutUrl,
        subtotalAmount = this.subtotalAmount,
        totalAmount = this.totalAmount,
        currency = this.currency,
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
