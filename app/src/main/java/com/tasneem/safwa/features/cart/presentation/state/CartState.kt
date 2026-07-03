package com.tasneem.safwa.features.cart.presentation.state

data class CartItem(
    val id: String,
    val productId: String,
    val title: String,
    val variant: String,
    val vendor: String,
    val price: Double,
    val currency: String,
    val quantity: Int,
    val imageUrl: String,
    val originalQuantity: Int = quantity
)

data class AppliedDiscountCode(
    val code: String,
    val applicable: Boolean
)

data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val promoCode: String = "",
    val appliedDiscountCodes: List<AppliedDiscountCode> = emptyList(),
    val promoCodeError: String? = null,
    val isApplyingPromoCode: Boolean = false,
    val shippingAmount: Double? = null,
    val subtotalAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val currency: String = "SAR",
    val removingItemIds: Set<String> = emptySet(),
    val updatingItemIds: Set<String> = emptySet(),
    val itemPendingRemoval: CartItem? = null,
    val promoCodePendingRemoval: String? = null,
    val errorMessage: String? = null
) {
    val totalItemCount: Int
        get() = items.sumOf { it.quantity }

    val isEmpty: Boolean
        get() = items.isEmpty()
}
