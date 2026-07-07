package com.tasneem.safwa.features.reviews.domain.repository

import com.tasneem.safwa.features.reviews.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(productId: String): Flow<List<Review>>
    suspend fun submitReview(review: Review): Result<Unit>
}