package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.core.domain.model.Product

sealed interface WishlistIntent {
    data class ToggleFavorite(val product: Product) : WishlistIntent
    data class ProductClicked(val product: Product) : WishlistIntent
    data class FilterSelected(val category: String) : WishlistIntent
    data object LoadWishlist : WishlistIntent
}
