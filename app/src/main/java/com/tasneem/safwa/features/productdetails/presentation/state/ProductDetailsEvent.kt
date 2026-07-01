package com.tasneem.safwa.features.productdetails.presentation.state

sealed interface ProductDetailsEvent {
    data class OptionSelected(val optionName: String, val value: String) : ProductDetailsEvent
    object ToggleWishlist : ProductDetailsEvent
    object AddToCartClicked : ProductDetailsEvent
    object BackClicked : ProductDetailsEvent
    object ShareClicked : ProductDetailsEvent
    object RetryClicked : ProductDetailsEvent
}