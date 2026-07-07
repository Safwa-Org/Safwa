package com.tasneem.safwa.features.reviews.data.model

data class ReviewEntity(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = 0L
)