package com.tasneem.safwa.features.brand.presentation.state

import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.features.brand.domain.model.Brand

data class BrandsUiState(
    val isLoading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val error: UiError? = null
)
