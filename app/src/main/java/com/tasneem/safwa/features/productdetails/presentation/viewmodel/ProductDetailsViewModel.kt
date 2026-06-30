package com.tasneem.safwa.features.productdetails.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.R
import com.tasneem.safwa.core.navigation.ScreenRoute
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
    private val detailsUseCase: GetProductDetailsUseCase,
    private val context: Application,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    private val productHandle: String = savedStateHandle.toRoute<ScreenRoute.ProductDetails>().handle

    init {
        loadProduct(productHandle)
    }

    private fun loadProduct(handle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val product = detailsUseCase(handle)
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
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: context.getString(R.string.something_went_wrong),
                    )
                }
            }
        }
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.OptionSelected -> {
                val newOptions = _state.value.selectedOptions + (event.optionName to event.value)
                val product = _state.value.product ?: return
                val matchedVariant = findMatchingVariant(product.variants, newOptions)
                _state.update {
                    it.copy(
                        selectedOptions = newOptions,
                        selectedVariantPrice = matchedVariant?.price?.let { p -> "${p.currency} ${p.amount}" },
                        isSelectedVariantAvailable = matchedVariant?.availableForSale ?: true,
                    )
                }
            }

            is ProductDetailsEvent.ToggleWishlist -> {
                _state.update { it.copy(isWishlisted = !it.isWishlisted) }
            }

            is ProductDetailsEvent.AddToCartClicked -> {
                TODO("implement add to cart")
            }

            is ProductDetailsEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(ProductDetailsEffect.NavigateBack)
                }
            }

            is ProductDetailsEvent.ShareClicked -> {
                val title = _state.value.product?.title ?: return
                val storeBaseUrl = com.tasneem.safwa.network.BuildConfig.SHOPIFY_ENDPOINT
                    .substringBefore("/api/")
                val url = "$storeBaseUrl/products/$productHandle"
                viewModelScope.launch {
                    _effect.send(ProductDetailsEffect.ShareProduct(title = title, url = url))
                }
            }
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
        variant.selectedOptions.all { option ->
            options[option.name] == option.value
        }
    }
}
