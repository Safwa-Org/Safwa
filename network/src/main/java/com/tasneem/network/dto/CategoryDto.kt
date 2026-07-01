package com.tasneem.network.dto

data class CategoryDto(
    val id: String,
    val title: String,
    val handle: String,
    val description: String?,
    val imageUrl: String?,
    val imageAlt: String?
)

