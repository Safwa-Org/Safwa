package com.tasneem.safwa.features.productdetails.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.R
import com.tasneem.safwa.core.exception.DomainException
import com.tasneem.safwa.core.navigation.ScreenRoute
import com.tasneem.safwa.core.presentation.mapper.toUiError
import com.tasneem.safwa.core.presentation.model.UiError
import com.tasneem.safwa.core.util.Resource
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
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
            is ProductDetailsEvent.AddToCartClicked -> { TODO("implement add to cart")
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
