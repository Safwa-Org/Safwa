package com.tasneem.network.dto.checkout

data class AdminGraphQLRequest(
    val query: String,
    val variables: Map<String, Any?> = emptyMap()
)

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
