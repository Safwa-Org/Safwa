package com.tasneem.safwa.features.brand.presentation.state

sealed interface BrandsUiEffect {
    data class NavigateToBrandDetails(val brandName: String) : BrandsUiEffect
    data object NavigateBack : BrandsUiEffect
}