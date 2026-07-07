package com.tasneem.safwa.features.productdetails.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.R
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.core.exception.DomainException
import com.tasneem.safwa.core.navigation.ScreenRoute
import com.tasneem.safwa.core.presentation.mapper.toUiError
import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.usecase.AddToCartUseCase
import com.tasneem.safwa.features.core.domain.usecase.GetWishlistUseCase
import com.tasneem.safwa.features.core.domain.usecase.ToggleFavoriteUseCase
import com.tasneem.safwa.features.productdetails.domain.mapper.toProduct
import com.tasneem.safwa.features.productdetails.domain.model.ProductDetails
import com.tasneem.safwa.features.productdetails.domain.model.ProductVariant
import com.tasneem.safwa.features.productdetails.domain.usecase.GetProductDetailsUseCase
import com.tasneem.safwa.features.productdetails.presentation.state.ProductDetailsEffect
import com.tasneem.safwa.features.productdetails.presentation.state.ProductDetailsEvent
import com.tasneem.safwa.features.productdetails.presentation.state.ProductDetailsState
import com.tasneem.safwa.features.productdetails.presentation.state.mapper.toUiModel
import com.tasneem.safwa.features.reviews.domain.model.Review
import com.tasneem.safwa.features.reviews.domain.usecase.GetProductReviewsUseCase
import com.tasneem.safwa.features.reviews.domain.usecase.SubmitReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getProductReviewsUseCase: GetProductReviewsUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val preferencesUseCases: PreferencesUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    private val productHandle: String =
        savedStateHandle.toRoute<ScreenRoute.ProductDetails>().handle

    private var loadedProduct: ProductDetails? = null

    init {
        loadProduct(productHandle)
        observeCurrentUserId()
    }
    private fun observeCurrentUserId() {
        viewModelScope.launch {
            preferencesUseCases.getUserSession().collect { user ->
                _state.update { it.copy(currentUserId = user?.id) }
            }
        }
    }
    private fun loadProduct(handle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                onProductLoaded(getProductDetailsUseCase(handle))
            } catch (e: DomainException) {
                onLoadError(e.toUiError())
            } catch (_: Exception) {
                onLoadError(UiError(R.string.something_went_wrong, R.string.error_generic_desc))
            }
        }
    }

    private fun onProductLoaded(product: ProductDetails) {
        loadedProduct = product
        val defaultOptions = resolveDefaultOptions(product)
        val matchedVariant = findMatchingVariant(product, defaultOptions)
        _state.update {
            it.copy(
                isLoading = false,
                product = product.toUiModel(),
                selectedOptions = defaultOptions,
                selectedVariantPrice = matchedVariant?.price?.let { p -> "${p.currency} ${p.amount}" },
                isSelectedVariantAvailable = matchedVariant?.availableForSale ?: true,
            )
        }
        observeWishlistStatus(product.id)
        observeReviews(product.id)
    }
    private fun observeReviews(productId: String) {
        viewModelScope.launch {
            getProductReviewsUseCase(productId).collect { reviews ->
                _state.update { it.copy(reviews = reviews) }
            }
        }
    }
    private fun onLoadError(error: UiError) {
        _state.update { it.copy(isLoading = false, error = error) }
    }

    private fun observeWishlistStatus(productId: String) {
        viewModelScope.launch {
            getWishlistUseCase().collect { resource ->
                if (resource is Resource.Success) {
                    _state.update { it.copy(isWishlisted = resource.data.any { p -> p.id == productId }) }
                }
            }
        }
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.OptionSelected -> onOptionSelected(event.optionName, event.value)
            is ProductDetailsEvent.ToggleWishlist -> onToggleWishlist()
            is ProductDetailsEvent.RetryClicked -> loadProduct(productHandle)
            is ProductDetailsEvent.BackClicked -> viewModelScope.launch {
                _effect.send(
                    ProductDetailsEffect.NavigateBack
                )
            }

            is ProductDetailsEvent.ShareClicked -> onShare()
            is ProductDetailsEvent.AddToCartClicked -> onAddToCart()

            is ProductDetailsEvent.ReviewRatingChanged -> {
                _state.update { it.copy(newReviewRating = event.rating) }
            }
            is ProductDetailsEvent.ReviewCommentChanged -> {
                _state.update { it.copy(newReviewComment = event.comment) }
            }
            is ProductDetailsEvent.WriteReviewClicked -> onWriteReviewClicked()
            is ProductDetailsEvent.CancelReviewClicked -> {
                _state.update {
                    it.copy(isReviewFormVisible = false, newReviewRating = 0, newReviewComment = "")
                }
            }
            is ProductDetailsEvent.SubmitReviewClicked -> onSubmitReview()
        }
    }
    private fun onWriteReviewClicked() {
        val currentUserId = _state.value.currentUserId
        val existingReview = _state.value.reviews.firstOrNull { it.userId == currentUserId }

        _state.update {
            it.copy(
                isReviewFormVisible = true,
                newReviewRating = existingReview?.rating ?: 0,
                newReviewComment = existingReview?.comment ?: ""
            )
        }
    }

    private fun onSubmitReview() {
        val product = loadedProduct ?: return
        viewModelScope.launch {
            val user = preferencesUseCases.getUserSession().first()
            if (user == null || user.isGuest) {
                _effect.send(ProductDetailsEffect.ShowSnackBar("Please sign in to leave a review"))
                return@launch
            }

            _state.update { it.copy(isSubmittingReview = true) }

            val review = Review(
                productId = product.id,
                userId = user.id,
                userName = "${user.firstName} ${user.lastName}".trim().ifBlank { "Anonymous" },
                rating = _state.value.newReviewRating,
                comment = _state.value.newReviewComment.trim()
            )

            val result = submitReviewUseCase(review)
            _state.update { it.copy(isSubmittingReview = false) }

            result.onSuccess {
                _state.update {
                    it.copy(newReviewRating = 0, newReviewComment = "", isReviewFormVisible = false)
                }
                _effect.send(ProductDetailsEffect.ShowSnackBar("Review submitted"))
            }.onFailure { error ->
                _effect.send(ProductDetailsEffect.ShowSnackBar(error.message ?: "Could not submit review"))
            }
        }
    }    private fun onAddToCart() {
        val product = loadedProduct ?: return
        val matchedVariant = findMatchingVariant(product, _state.value.selectedOptions) ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }
            val result = addToCartUseCase(matchedVariant.id)
            _state.update { it.copy(isAddingToCart = false) }
            
            when (result) {
                is Resource.Success -> {
                    _effect.send(ProductDetailsEffect.ShowSnackBar("Added to cart successfully"))
                }
                is Resource.Error -> {
                    _effect.send(ProductDetailsEffect.ShowSnackBar(result.message ?: "Failed to add to cart"))
                }
                else -> Unit
            }
        }
    }

    private fun onOptionSelected(optionName: String, value: String) {
        val newOptions = _state.value.selectedOptions + (optionName to value)
        val product = _state.value.product ?: return
        val matchedVariant = findMatchingVariant(product.variants, newOptions)
        _state.update {
            it.copy(
                selectedOptions = newOptions,
                selectedVariantPrice = matchedVariant?.price?.let { price -> "${price.currency} ${price.amount}" },
                isSelectedVariantAvailable = matchedVariant?.availableForSale ?: true,
            )
        }
    }

    private fun onToggleWishlist() {
        val product = loadedProduct?.toProduct() ?: return
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(product)
            if (result is Resource.Error) {
                _effect.send(ProductDetailsEffect.ShowSnackBar(result.message ?: ""))
            }
        }
    }

    private fun onShare() {
        val title = _state.value.product?.title ?: return
        val storeBaseUrl =
            com.tasneem.safwa.network.BuildConfig.SHOPIFY_ENDPOINT.substringBefore("/api/")
        val url = "$storeBaseUrl/products/$productHandle"
        viewModelScope.launch {
            _effect.send(ProductDetailsEffect.ShareProduct(title = title, url = url))
        }
    }

    private fun resolveDefaultOptions(product: ProductDetails): Map<String, String> {
        val firstAvailableVariant = product.variants.firstOrNull { it.availableForSale }
            ?: product.variants.firstOrNull()
        return firstAvailableVariant?.selectedOptions
            ?.associate { it.name to it.value }
            ?: emptyMap()
    }

    private fun findMatchingVariant(
        product: ProductDetails,
        options: Map<String, String>,
    ): ProductVariant? = findMatchingVariant(product.variants, options)

    private fun findMatchingVariant(
        variants: List<ProductVariant>,
        options: Map<String, String>,
    ): ProductVariant? = variants.firstOrNull { variant ->
        variant.selectedOptions.all { option -> options[option.name] == option.value }
    }
}
