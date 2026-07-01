package com.tasneem.safwa.features.cart.presentation.state

sealed interface CartEffect {
    data object NavigateBack : CartEffect
    data object NavigateToCheckout : CartEffect
    data object NavigateToWishlist : CartEffect
    data class ShowSnackBar(val message: String) : CartEffect
}
