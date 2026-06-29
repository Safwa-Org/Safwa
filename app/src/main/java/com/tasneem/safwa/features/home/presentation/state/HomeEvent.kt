package com.tasneem.safwa.features.home.presentation.state

import com.tasneem.safwa.features.wishlist.domain.model.Product

sealed interface HomeEvent {
    data object LoadHome : HomeEvent
    data class CategorySelected(val category: String) : HomeEvent
    data class ToggleFavorite(val product: Product) : HomeEvent
    data class ProductClicked(val product: Product) : HomeEvent
    data class BrandClicked(val brand: String) : HomeEvent
    data object CartClicked : HomeEvent
    data object ViewAllCategories : HomeEvent
    data object SeeAllBestSellers : HomeEvent
    data object ViewAllBrands : HomeEvent
    data object ShopEditClicked : HomeEvent
}
