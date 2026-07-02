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
    val imageUrl: String
)

data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val promoCode: String = "",
    val appliedPromoCode: String? = null,
    val promoDiscount: Double = 0.0,
    val removingItemIds: Set<String> = emptySet(),
    val itemPendingRemoval: CartItem? = null,
    val errorMessage: String? = null
) {
    val totalItemCount: Int
        get() = items.sumOf { it.quantity }

    val subtotal: Double
        get() = items.sumOf { it.price * it.quantity }

    val vat: Double
        get() = subtotal * 0.15

    val total: Double
        get() = subtotal + vat - promoDiscount

    val isEmpty: Boolean
        get() = items.isEmpty()
}
