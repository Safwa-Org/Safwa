package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderLineItem
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.checkout.domain.model.OrderSummary
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import javax.inject.Inject

class CreateDraftOrderUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(
        cart: Cart,
        summary: OrderSummary,
        shippingAddress: Address,
        customerEmail: String?
    ): Resource<DraftOrder> {
        val request = DraftOrderRequest(
            email = customerEmail,
            lineItems = cart.lines.map { line ->
                DraftOrderLineItem(variantId = line.variantId, quantity = line.quantity)
            },
            shippingAddress = shippingAddress,
            discountAmount = summary.discount.takeIf { it > 0.0 },
            discountCode = summary.appliedDiscountCode,
            shippingLineTitle = cart.shippingTitle,
            shippingLineAmount = summary.shipping,
            note = "Placed via Safwa Android app"
        )
        return checkoutRepository.createDraftOrder(request)
    }
}
