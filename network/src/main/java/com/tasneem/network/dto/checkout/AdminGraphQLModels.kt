package com.tasneem.network.dto.checkout

data class GraphQLResponseDto<T>(
    val data: T?,
    val errors: List<GraphQLErrorDto>? = null
)

data class GraphQLErrorDto(
    val message: String
)

data class UserErrorDto(
    val field: List<String>? = null,
    val message: String
)
