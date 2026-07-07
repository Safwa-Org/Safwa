package com.tasneem.safwa.features.reviews.domain.model

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,       // 1..5
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)