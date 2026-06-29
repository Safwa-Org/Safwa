package com.tasneem.safwa.features.search.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

data class SearchState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false
)
