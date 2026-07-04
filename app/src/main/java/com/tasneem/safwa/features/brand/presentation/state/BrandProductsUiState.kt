package com.tasneem.safwa.features.brand.presentation.state

import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.features.core.domain.model.Product

data class BrandProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val error: UiError? = null
)
