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
    val isLoading: Boolean = false,
    val availableBrands: List<String> = emptyList(),
    val availableSubCategories: List<String> = emptyList(),
    val selectedBrands: Set<String> = emptySet(),
    val selectedSubCategories: Set<String> = emptySet(),
    val selectedSortOption: SortOption = SortOption.NONE,
    val showFilterSheet: Boolean = false,
    val isGroupedBySubCategory: Boolean = false,
    val maxPrice: Float = 1000f,
    val selectedPriceRange: ClosedFloatingPointRange<Float> = 0f..1000f
)

enum class SortOption {
    NONE,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    BEST_SELLER
}
