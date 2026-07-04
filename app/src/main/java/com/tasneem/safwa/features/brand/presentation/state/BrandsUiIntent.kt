package com.tasneem.safwa.features.brand.presentation.state

import com.tasneem.safwa.features.brand.domain.model.Brand

sealed interface BrandsUiIntent {
    object LoadBrands : BrandsUiIntent
    data class OnBrandClick(val brand: Brand) : BrandsUiIntent
    object OnBackClick : BrandsUiIntent
}