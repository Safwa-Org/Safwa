package com.tasneem.safwa.features.productdetails.presentation.state

import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.ProductDetailsUiModel

data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: ProductDetailsUiModel? = null,
    val selectedOptions: Map<String, String> = emptyMap(),
    val selectedVariantPrice: String? = null,
    val isSelectedVariantAvailable: Boolean = true,
    val isWishlisted: Boolean = false,
    val isAddingToCart: Boolean = false,
    val error: UiError? = null,
)
