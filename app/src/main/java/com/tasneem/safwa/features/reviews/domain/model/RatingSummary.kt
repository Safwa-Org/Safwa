package com.tasneem.safwa.features.reviews.domain.model

data class RatingSummary(
    val average: Float,
    val count: Int,
) {
    val hasRatings: Boolean get() = count > 0

    companion object {
        val EMPTY = RatingSummary(average = 0f, count = 0)

        fun from(reviews: List<Review>): RatingSummary {
            if (reviews.isEmpty()) return EMPTY
            return RatingSummary(
                average = reviews.sumOf { it.rating }.toFloat() / reviews.size,
                count = reviews.size,
            )
        }
    }
}
