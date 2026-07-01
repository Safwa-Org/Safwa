package com.tasneem.safwa.features.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class CartViewModel @Inject constructor() : ViewModel() {

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
                _state.update { it.copy(isLoading = true) }
                val mockItems = listOf(
                    CartItem(
                        id = "cart_1",
                        productId = "1",
                        title = "Nuit d'Or EDP",
                        variant = "50 ml",
                        vendor = "MAISON",
                        price = 480.0,
                        currency = "SAR",
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1541643600914-78b084683601"
                    ),
                    CartItem(
                        id = "cart_2",
                        productId = "2",
                        title = "Vermilion Bifold",
                        variant = "Saddle",
                        vendor = "ATELIER",
                        price = 320.0,
                        currency = "SAR",
                        quantity = 2,
                        imageUrl = "https://images.unsplash.com/photo-1627123424574-724758594e93"
                    )
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        items = mockItems
                    )
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
