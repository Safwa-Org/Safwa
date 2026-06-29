package com.tasneem.safwa.features.productdetails.presentation.state

import com.tasneem.safwa.features.productdetails.presentation.state.mapper.ProductUiModel

data class ProductDetailsState(
    val isLoading: Boolean = false,
    val product: ProductUiModel? = null,
    val selectedSize: String = "",
    val isWishlisted: Boolean = false,
    val errorMessage: String? = null
)



