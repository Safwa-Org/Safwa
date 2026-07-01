package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category

sealed interface WishlistIntent {
    data class ToggleFavorite(val product: Product) : WishlistIntent
    data class ProductClicked(val product: Product) : WishlistIntent
    data class FilterSelected(val category: Category) : WishlistIntent
    data object LoadWishlist : WishlistIntent
}
