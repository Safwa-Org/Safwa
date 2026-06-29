package com.tasneem.safwa.features.search.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    data class FilterSelected(val category: String) : SearchEvent()
    data class ToggleFavorite(val product: Product) : SearchEvent()
    data class ProductClicked(val product: Product) : SearchEvent()
    object ExecuteSearch : SearchEvent()
}
