package com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state

sealed interface ProductDetailsEvent {
    data class LoadProduct(val productId: String) : ProductDetailsEvent
    data class SizeSelected(val size: String) : ProductDetailsEvent
    object ToggleWishlist : ProductDetailsEvent
    object AddToCartClicked : ProductDetailsEvent
    object BackClicked : ProductDetailsEvent
    object ShareClicked : ProductDetailsEvent
}