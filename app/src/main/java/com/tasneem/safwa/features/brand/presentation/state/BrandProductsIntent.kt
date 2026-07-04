package com.tasneem.safwa.features.brand.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product

sealed interface BrandProductsIntent {
    object LoadProducts : BrandProductsIntent
    data class OnProductClick(val product: Product) : BrandProductsIntent
    data class ToggleFavorite(val product: Product) : BrandProductsIntent
    object OnBackClick : BrandProductsIntent
}
