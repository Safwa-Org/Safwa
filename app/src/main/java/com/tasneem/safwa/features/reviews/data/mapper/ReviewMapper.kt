package com.tasneem.safwa.features.reviews.data.mapper

import com.tasneem.safwa.features.reviews.data.model.ReviewEntity
import com.tasneem.safwa.features.reviews.domain.model.Review

fun ReviewEntity.toDomain() = Review(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun Review.toEntity() = ReviewEntity(
    id = id,
    productId = productId,
    userId = userId,
    userName = userName,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)