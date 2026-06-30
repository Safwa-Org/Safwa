package com.tasneem.safwa.features.home.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product

enum class GreetingType {
    MORNING, AFTERNOON, EVENING
}

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val greeting: GreetingType = GreetingType.MORNING,
    val editorialNumber: Int = 2,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val brands: List<String> = emptyList(),
    val errorMessage: String? = null
)
