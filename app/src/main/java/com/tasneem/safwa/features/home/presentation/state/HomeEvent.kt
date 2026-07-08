package com.tasneem.safwa.features.home.presentation.state

import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.category.domain.model.Category

sealed interface HomeEvent {
    data object LoadHome : HomeEvent
    data object Refresh : HomeEvent
    data class CategorySelected(val category: Category) : HomeEvent
    data class ToggleFavorite(val product: Product) : HomeEvent
    data class ProductClicked(val product: Product) : HomeEvent
    data class BrandClicked(val brand: String) : HomeEvent
    data object CartClicked : HomeEvent
    data object ViewAllCategories : HomeEvent
    data object SeeAllBestSellers : HomeEvent
    data object ViewAllBrands : HomeEvent
    data object ShopEditClicked : HomeEvent
    data object ViewAllLatestProducts : HomeEvent
}
