package com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks.call
import com.tasneem.safwa.features.wishlist.domain.model.Product
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.mapper.toUiModel
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsEffect
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsEvent
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsState
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
class ProductDetailsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.LoadProduct -> {
                TODO()
            }

            is ProductDetailsEvent.SizeSelected -> {
                _state.update { it.copy(selectedSize = event.size) }
            }

            is ProductDetailsEvent.ToggleWishlist -> {
                _state.update { it.copy(isWishlisted = !it.isWishlisted) }
            }

            is ProductDetailsEvent.AddToCartClicked -> {
                TODO()
            }

            is ProductDetailsEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(ProductDetailsEffect.NavigateBack)
                }
            }

            is ProductDetailsEvent.ShareClicked -> {
                TODO()
            }
        }
    }
}