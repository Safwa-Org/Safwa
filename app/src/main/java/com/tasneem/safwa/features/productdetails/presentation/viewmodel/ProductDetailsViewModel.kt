package com.tasneem.safwa.features.productdetails.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.R
import com.tasneem.safwa.core.navigation.ScreenRoute
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val handle = savedStateHandle.toRoute<ScreenRoute.ProductDetails>().handle
        loadProduct(handle)
    }

    private fun loadProduct(handle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val product = detailsUseCase(handle)
                val firstSize = product.variants
                    .flatMap { it.selectedOptions }
                    .firstOrNull { it.name.equals("Size", ignoreCase = true) }
                    ?.value ?: product.variants.firstOrNull()?.title.orEmpty()
                _state.update {
                    it.copy(
                        isLoading = false,
                        product = product.toUiModel(),
                        selectedSize = firstSize
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.SizeSelected -> {
                _state.update { it.copy(selectedSize = event.size) }
            }

            is ProductDetailsEvent.ToggleWishlist -> {
                _state.update { it.copy(isWishlisted = !it.isWishlisted) }
            }

            is ProductDetailsEvent.AddToCartClicked -> {
                TODO("Implement cart")
            }

            is ProductDetailsEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(ProductDetailsEffect.NavigateBack)
                }
            }

            is ProductDetailsEvent.ShareClicked -> {
                TODO("Implement share")
            }
        }
    }
}