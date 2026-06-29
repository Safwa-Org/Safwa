package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

sealed interface WishlistEvent {
    data class ToggleFavorite(val product: Product) : WishlistEvent
    data class ProductClicked(val product: Product) : WishlistEvent
    data object LoadWishlist : WishlistEvent
}
