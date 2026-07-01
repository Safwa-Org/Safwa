package com.tasneem.safwa.features.home.presentation.state

sealed interface HomeEffect {
    data object NavigateToSearch : HomeEffect
    data object NavigateToCart : HomeEffect
    data class NavigateToProductDetails(val handle: String) : HomeEffect
    data class NavigateToBrand(val brand: String) : HomeEffect
}
