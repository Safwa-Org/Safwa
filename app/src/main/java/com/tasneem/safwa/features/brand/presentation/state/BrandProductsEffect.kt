package com.tasneem.safwa.features.brand.presentation.state

sealed interface BrandProductsEffect {
    data class NavigateToProductDetails(val handle: String) : BrandProductsEffect
    object NavigateBack : BrandProductsEffect
}
