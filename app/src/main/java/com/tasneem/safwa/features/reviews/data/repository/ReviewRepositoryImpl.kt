package com.tasneem.safwa.features.reviews.data.repository

import com.tasneem.safwa.features.reviews.data.datasource.ReviewRemoteDataSource
import com.tasneem.safwa.features.reviews.data.mapper.toDomain
import com.tasneem.safwa.features.reviews.data.mapper.toEntity
import com.tasneem.safwa.features.reviews.domain.model.Review
import com.tasneem.safwa.features.reviews.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val remote: ReviewRemoteDataSource
) : ReviewRepository {

    override fun observeReviews(productId: String): Flow<List<Review>> {
        return remote.observeReviews(productId)
            .map { list -> list.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override suspend fun submitReview(review: Review): Result<Unit> = runCatching {
        remote.submitReview(review.toEntity())
    }
}