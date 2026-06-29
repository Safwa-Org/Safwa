package com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state

sealed interface ProductDetailsEffect {
    object NavigateBack : ProductDetailsEffect
    data class ShowSnackBar(val message: String) : ProductDetailsEffect
}