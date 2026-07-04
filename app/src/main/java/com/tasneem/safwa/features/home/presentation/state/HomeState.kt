package com.tasneem.safwa.features.home.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category
import com.tasneem.safwa.features.home.domain.model.PromoBanner

enum class GreetingType {
    MORNING, AFTERNOON, EVENING
}

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val greeting: GreetingType = GreetingType.MORNING,
    val promoBanners: List<PromoBanner> = emptyList(),
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val brands: List<String> = emptyList(),
    val cartItemCount: Int = 0,
    val errorMessage: String? = null
)
