package com.tasneem.safwa.features.brand.presentation.state

sealed interface BrandsUiEffect {
    data class NavigateToBrandDetails(val brandName: String) : BrandsUiEffect
    object NavigateBack : BrandsUiEffect
}