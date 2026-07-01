package com.tasneem.safwa.features.search.presentation

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category

data class SearchState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false
)
