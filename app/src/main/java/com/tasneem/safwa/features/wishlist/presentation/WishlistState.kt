package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

data class WishlistState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
