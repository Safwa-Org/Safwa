package com.tasneem.safwa.features.search.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class FilterSelected(val category: String) : SearchIntent
    data class ToggleFavorite(val product: Product) : SearchIntent
    data class ProductClicked(val product: Product) : SearchIntent
    data object ExecuteSearch : SearchIntent
}
