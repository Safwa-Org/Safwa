package com.tasneem.safwa.features.search.presentation

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class FilterSelected(val category: Category) : SearchIntent
    data class ToggleFavorite(val product: Product) : SearchIntent
    data class ProductClicked(val product: Product) : SearchIntent
    data object ExecuteSearch : SearchIntent
}
