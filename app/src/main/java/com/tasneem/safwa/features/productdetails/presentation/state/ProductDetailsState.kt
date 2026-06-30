package com.tasneem.safwa.features.productdetails.presentation.state

import com.tasneem.safwa.features.productdetails.presentation.state.mapper.ProductDetailsUiModel

data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: ProductDetailsUiModel? = null,
    val selectedSize: String = "",
    val isWishlisted: Boolean = false,
    val errorMessage: String? = null
)
