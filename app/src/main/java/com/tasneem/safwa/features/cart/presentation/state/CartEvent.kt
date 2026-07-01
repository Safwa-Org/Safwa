package com.tasneem.safwa.features.cart.presentation.state

sealed interface CartEvent {
    data object LoadCart : CartEvent
    data class IncreaseQuantity(val itemId: String) : CartEvent
    data class DecreaseQuantity(val itemId: String) : CartEvent
    data class RemoveItem(val itemId: String) : CartEvent
    data class PromoCodeChanged(val code: String) : CartEvent
    data object ApplyPromoCode : CartEvent
    data object ProceedToCheckout : CartEvent
    data object BackClicked : CartEvent
    data object WishlistClicked : CartEvent
}
