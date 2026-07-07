package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.checkout.domain.model.OrderSummary
import javax.inject.Inject

class CalculateOrderSummaryUseCase @Inject constructor() {

    operator fun invoke(cart: Cart): OrderSummary {
        val subtotal = cart.subtotalAmount.toDoubleOrNull() ?: 0.0
        val total = cart.totalAmount.toDoubleOrNull() ?: 0.0
        val tax = cart.taxAmount?.toDoubleOrNull()
        val shipping = cart.shippingAmount?.toDoubleOrNull()

        val discount = (subtotal - total + (tax ?: 0.0) + (shipping ?: 0.0))
            .coerceAtLeast(0.0)

        return OrderSummary(
            subtotal = subtotal,
            discount = discount,
            appliedDiscountCode = cart.discountCodes.firstOrNull { it.applicable }?.code,
            shipping = shipping,
            tax = tax,
            total = total,
            currency = cart.currency
        )
    }
}
