package com.tasneem.safwa.features.productdetails.presentation.state

import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.ProductDetailsUiModel
import com.tasneem.safwa.features.reviews.domain.model.RatingSummary
import com.tasneem.safwa.features.reviews.domain.model.Review

data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: ProductDetailsUiModel? = null,
    val selectedOptions: Map<String, String> = emptyMap(),
    val selectedVariantPrice: String? = null,
    val isSelectedVariantAvailable: Boolean = true,
    val isWishlisted: Boolean = false,
    val isAddingToCart: Boolean = false,
    val error: UiError? = null,
    val isGeneratingAiDescription: Boolean = false,
    val isDescriptionAiGenerated: Boolean = false,

    // Reviews
    val reviews: List<Review> = emptyList(),
    val currentUserId: String? = null,
    val newReviewRating: Int = 0,
    val newReviewComment: String = "",
    val isSubmittingReview: Boolean = false,
    val isReviewFormVisible: Boolean = false,
) {
    val ratingSummary: RatingSummary get() = RatingSummary.from(reviews)
}