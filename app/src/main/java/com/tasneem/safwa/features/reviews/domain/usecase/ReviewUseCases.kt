package com.tasneem.safwa.features.reviews.domain.usecase

import com.tasneem.safwa.features.reviews.domain.model.Review
import com.tasneem.safwa.features.reviews.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductReviewsUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    operator fun invoke(productId: String): Flow<List<Review>> = repo.observeReviews(productId)
}

class SubmitReviewUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(review: Review): Result<Unit> {
        if (review.rating !in 1..5) {
            return Result.failure(IllegalArgumentException("Please select a star rating."))
        }
        if (review.comment.isBlank()) {
            return Result.failure(IllegalArgumentException("Please write a comment."))
        }
        return repo.submitReview(review)
    }
}