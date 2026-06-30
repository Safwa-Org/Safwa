package com.tasneem.safwa.features.productdetails.presentation.state

sealed interface ProductDetailsEvent {
    data class SizeSelected(val size: String) : ProductDetailsEvent
    object ToggleWishlist : ProductDetailsEvent
    object AddToCartClicked : ProductDetailsEvent
    object BackClicked : ProductDetailsEvent
    object ShareClicked : ProductDetailsEvent
}