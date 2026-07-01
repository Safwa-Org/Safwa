package com.tasneem.safwa.features.category.presentation.categories.state

import com.tasneem.safwa.features.category.domain.model.Category

data class CategoriesState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

