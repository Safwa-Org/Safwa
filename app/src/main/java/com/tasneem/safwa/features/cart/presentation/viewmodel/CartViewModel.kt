package com.tasneem.safwa.features.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.usecase.ApplyDiscountCodeUseCase
import com.tasneem.safwa.features.cart.presentation.state.AppliedDiscountCode
import com.tasneem.safwa.features.cart.presentation.state.CartEffect
import com.tasneem.safwa.features.cart.presentation.state.CartEvent
import com.tasneem.safwa.features.cart.presentation.state.CartItem
import com.tasneem.safwa.features.cart.presentation.state.CartState
import com.tasneem.safwa.features.cart.domain.usecase.GetCartUseCase
import com.tasneem.safwa.features.cart.domain.usecase.RemoveFromCartUseCase
import com.tasneem.safwa.features.cart.domain.usecase.UpdateCartLineUseCase
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
    private val getCartUseCase: GetCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartLineUseCase: UpdateCartLineUseCase,
    private val applyDiscountCodeUseCase: ApplyDiscountCodeUseCase
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
                                items = cartItems,
                                shippingAmount = cart.shippingAmount?.toDoubleOrNull(),
                                subtotalAmount = cart.subtotalAmount.toDoubleOrNull() ?: 0.0,
                                totalAmount = cart.totalAmount.toDoubleOrNull() ?: 0.0,
                                appliedDiscountCodes = cart.discountCodes.map { discount ->
                                    AppliedDiscountCode(discount.code, discount.applicable)
                                },
                                currency = cart.currency
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

            is CartEvent.ApplyQuantityUpdate -> {
                val itemToUpdate = _state.value.items.find { it.id == event.itemId } ?: return
                if (itemToUpdate.quantity == itemToUpdate.originalQuantity) return
                
                val difference = itemToUpdate.quantity - itemToUpdate.originalQuantity
                _state.update { it.copy(updatingItemIds = it.updatingItemIds + event.itemId) }
                
                viewModelScope.launch {
                    val result = updateCartLineUseCase(itemToUpdate.id, itemToUpdate.quantity, difference)
                    if (result is Resource.Success) {
                        _state.update { currentState ->
                            val updatedItems = currentState.items.map { item ->
                                if (item.id == event.itemId) item.copy(originalQuantity = item.quantity)
                                else item
                            }
                            val newSubtotal = updatedItems.sumOf { it.price * it.quantity }
                            val newTotal = result.data?.toDoubleOrNull() ?: newSubtotal

                            currentState.copy(
                                updatingItemIds = currentState.updatingItemIds - event.itemId,
                                items = updatedItems,
                                subtotalAmount = newSubtotal,
                                totalAmount = newTotal
                            )
                        }
                        _effect.send(CartEffect.ShowSnackBar("Quantity updated successfully"))
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                updatingItemIds = currentState.updatingItemIds - event.itemId,
                                items = currentState.items.map { item ->
                                    if (item.id == event.itemId) item.copy(quantity = item.originalQuantity)
                                    else item
                                },
                                errorMessage = (result as? Resource.Error)?.message ?: "Failed to update item"
                            )
                        }
                        _effect.send(CartEffect.ShowSnackBar("Failed to update quantity"))
                    }
                }
            }

            is CartEvent.RemoveItemClicked -> {
                val item = _state.value.items.find { it.id == event.itemId }
                _state.update { it.copy(itemPendingRemoval = item) }
            }

            is CartEvent.CancelRemoveItem -> {
                _state.update { it.copy(itemPendingRemoval = null) }
            }

            is CartEvent.ConfirmRemoveItem -> {
                val itemToRemove = _state.value.itemPendingRemoval ?: return
                
                _state.update {
                    it.copy(
                        itemPendingRemoval = null,
                        removingItemIds = it.removingItemIds + itemToRemove.id
                    ) 
                }
                
                viewModelScope.launch {
                    val result = removeFromCartUseCase(itemToRemove.id, itemToRemove.quantity)
                    if (result is Resource.Success) {
                        _state.update { currentState ->
                            val updatedItems = currentState.items.filter { it.id != itemToRemove.id }
                            val newSubtotal = updatedItems.sumOf { it.price * it.quantity }
                            val newTotal = result.data?.toDoubleOrNull() ?: newSubtotal

                            currentState.copy(
                                removingItemIds = currentState.removingItemIds - itemToRemove.id,
                                items = updatedItems,
                                subtotalAmount = newSubtotal,
                                totalAmount = newTotal
                            )
                        }
                        _effect.send(CartEffect.ShowSnackBar("Item removed successfully"))
                    } else {
                        _state.update {
                            it.copy(
                                removingItemIds = it.removingItemIds - itemToRemove.id,
                                errorMessage = (result as? Resource.Error)?.message ?: "Failed to remove item"
                            )
                        }
                        _effect.send(CartEffect.ShowSnackBar("Failed to remove item"))
                    }
                }
            }

            is CartEvent.PromoCodeChanged -> {
                _state.update { it.copy(promoCode = event.code, promoCodeError = null) }
            }

            is CartEvent.ApplyPromoCode -> {
                val code = _state.value.promoCode.trim()
                if (code.isBlank()) return
                
                val currentValidCodes = _state.value.appliedDiscountCodes.filter { it.applicable }.map { it.code }
                if (currentValidCodes.any { it.equals(code, ignoreCase = true) }) {
                     _state.update { it.copy(promoCodeError = "Code already applied") }
                     return
                }
                
                val codesToApply = currentValidCodes + code
                
                _state.update { it.copy(isApplyingPromoCode = true, promoCodeError = null) }
                
                viewModelScope.launch {
                    val result = applyDiscountCodeUseCase(codesToApply)
                    if (result is Resource.Success) {
                        val appliedCodes = result.data?.discountCodes ?: emptyList()
                        val newCode = appliedCodes.find { it.code.equals(code, ignoreCase = true) }
                        
                        val validCodesFromBackend = appliedCodes.filter { it.applicable }.map { discount ->
                            AppliedDiscountCode(discount.code, discount.applicable)
                        }
                        
                        if (newCode != null && newCode.applicable) {
                            _state.update { 
                                it.copy(
                                    isApplyingPromoCode = false,
                                    promoCode = "",
                                    appliedDiscountCodes = validCodesFromBackend,
                                    subtotalAmount = result.data.subtotalAmount.toDoubleOrNull() ?: it.subtotalAmount,
                                    totalAmount = result.data.totalAmount.toDoubleOrNull() ?: it.totalAmount
                                ) 
                            }
                            _effect.send(CartEffect.ShowSnackBar("Promo code applied!"))
                        } else {
                             _state.update { 
                                it.copy(
                                    isApplyingPromoCode = false,
                                    promoCodeError = "Invalid discount code or gift card",
                                    appliedDiscountCodes = validCodesFromBackend,
                                    subtotalAmount = result.data?.subtotalAmount?.toDoubleOrNull() ?: it.subtotalAmount,
                                    totalAmount = result.data?.totalAmount?.toDoubleOrNull() ?: it.totalAmount
                                ) 
                            }
                        }
                    } else {
                        _state.update { 
                            it.copy(
                                isApplyingPromoCode = false,
                                promoCodeError = (result as? Resource.Error)?.message ?: "Failed to apply promo code"
                            ) 
                        }
                    }
                }
            }

            is CartEvent.RemovePromoCode -> {
                _state.update { it.copy(promoCodePendingRemoval = event.code) }
            }

            is CartEvent.CancelRemovePromoCode -> {
                _state.update { it.copy(promoCodePendingRemoval = null) }
            }

            is CartEvent.ConfirmRemovePromoCode -> {
                val codeToRemove = _state.value.promoCodePendingRemoval ?: return
                _state.update { it.copy(promoCodePendingRemoval = null) }
                
                val currentValidCodes = _state.value.appliedDiscountCodes
                    .filter { it.applicable && it.code != codeToRemove }
                    .map { it.code }

                viewModelScope.launch {
                    val result = applyDiscountCodeUseCase(currentValidCodes)
                    if (result is Resource.Success) {
                        val appliedCodes = result.data?.discountCodes ?: emptyList()
                        val validCodesFromBackend = appliedCodes.filter { it.applicable }.map { discount ->
                            AppliedDiscountCode(discount.code, discount.applicable)
                        }

                        _state.update { it.copy(
                            appliedDiscountCodes = validCodesFromBackend,
                            subtotalAmount = result.data.subtotalAmount.toDoubleOrNull() ?: it.subtotalAmount,
                            totalAmount = result.data.totalAmount.toDoubleOrNull() ?: it.totalAmount
                        )}
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
