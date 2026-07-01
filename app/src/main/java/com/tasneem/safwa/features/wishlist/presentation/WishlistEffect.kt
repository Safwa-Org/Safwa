package com.tasneem.safwa.features.wishlist.presentation

sealed interface WishlistEffect {
    data class NavigateToProductDetails(val handle: String) : WishlistEffect
}
