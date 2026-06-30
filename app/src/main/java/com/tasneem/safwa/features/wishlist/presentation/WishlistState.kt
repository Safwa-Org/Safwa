package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.core.domain.model.Product

data class WishlistState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<String> = listOf("All"),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)
