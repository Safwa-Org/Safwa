package com.tasneem.network.dto

data class PaginatedResultDto<T>(
    val items: List<T>,
    val endCursor: String?,
    val hasNextPage: Boolean
)
