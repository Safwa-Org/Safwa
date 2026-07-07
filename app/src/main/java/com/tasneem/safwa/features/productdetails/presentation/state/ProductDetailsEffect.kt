package com.tasneem.safwa.features.productdetails.presentation.state

sealed interface ProductDetailsEffect {
    object NavigateBack : ProductDetailsEffect
    object NavigateToCart : ProductDetailsEffect
    data class ShowSnackBar(val message: String) : ProductDetailsEffect
    data class ShareProduct(val title: String, val url: String) : ProductDetailsEffect
}