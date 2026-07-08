package com.tasneem.safwa.features.core.domain.model

data class PaginatedData<T>(
    val items: List<T>,
    val endCursor: String?,
    val hasNextPage: Boolean
)
