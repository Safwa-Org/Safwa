package com.tasneem.safwa.features.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.presentation.state.CartEffect
import com.tasneem.safwa.features.cart.presentation.state.CartEvent
import com.tasneem.safwa.features.cart.presentation.state.CartItem
import com.tasneem.safwa.features.cart.presentation.state.CartState
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
class CartViewModel @Inject constructor(
    private val getCartUseCase: com.tasneem.safwa.features.cart.domain.usecase.GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _effect = Channel<CartEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(CartEvent.LoadCart)
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.LoadCart -> {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                viewModelScope.launch {
                    val result = getCartUseCase()
                    if (result is Resource.Success) {
                        val cart = result.data
                        val cartItems = cart.lines.map { line ->
                            CartItem(
                                id = line.id,
                                productId = line.productId,
                                title = line.productTitle,
                                variant = line.variantTitle,
                                vendor = line.vendor,
                                price = line.price.toDoubleOrNull() ?: 0.0,
                                currency = line.currency,
                                quantity = line.quantity,
                                imageUrl = line.imageUrl
                            )
                        }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                items = cartItems
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = (result as? com.tasneem.safwa.core.util.Resource.Error)?.message ?: "Failed to load cart"
                            )
                        }
                    }
                }
            }

            is CartEvent.IncreaseQuantity -> {
                _state.update { currentState ->
                    currentState.copy(
                        items = currentState.items.map { item ->
                            if (item.id == event.itemId) item.copy(quantity = item.quantity + 1)
                            else item
                        }
                    )
                }
            }

            is CartEvent.DecreaseQuantity -> {
                _state.update { currentState ->
                    val updatedItems = currentState.items.map { item ->
                        if (item.id == event.itemId && item.quantity > 1) {
                            item.copy(quantity = item.quantity - 1)
                        } else {
                            item
                        }
                    }
                    currentState.copy(items = updatedItems)
                }
            }

            is CartEvent.RemoveItem -> {
                _state.update { currentState ->
                    currentState.copy(
                        items = currentState.items.filter { it.id != event.itemId }
                    )
                }
            }

            is CartEvent.PromoCodeChanged -> {
                _state.update { it.copy(promoCode = event.code) }
            }

            is CartEvent.ApplyPromoCode -> {
                val code = _state.value.promoCode
                if (code.equals("Ashraf", ignoreCase = true)) {
                    _state.update {
                        it.copy(
                            appliedPromoCode = code.uppercase(),
                            promoDiscount = 50.0
                        )
                    }
                    viewModelScope.launch {
                        _effect.send(CartEffect.ShowSnackBar("Promo code applied!"))
                    }
                } else {
                    viewModelScope.launch {
                        _effect.send(CartEffect.ShowSnackBar("Invalid promo code"))
                    }
                }
            }

            is CartEvent.ProceedToCheckout -> {
                viewModelScope.launch {
                    _effect.send(CartEffect.NavigateToCheckout)
                }
            }

            is CartEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(CartEffect.NavigateBack)
                }
            }

            is CartEvent.WishlistClicked -> {
                viewModelScope.launch {
                    _effect.send(CartEffect.NavigateToWishlist)
                }
            }
        }
    }
}
