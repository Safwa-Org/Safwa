package com.tasneem.safwa.features.category.presentation.category_products.state

import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.features.core.domain.model.Product

data class CategoryProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val error: UiError? = null
)
