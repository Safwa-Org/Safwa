package com.tasneem.safwa.features.search.presentation

sealed interface SearchEffect {
    data class NavigateToProductDetails(val handle: String) : SearchEffect
}
