package com.tasneem.safwa.features.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.usecase.GetSavedCardsUseCase
import com.tasneem.safwa.features.payment.domain.usecase.SaveCardUseCase
import com.tasneem.safwa.features.payment.presentation.state.PaymentEffect
import com.tasneem.safwa.features.payment.presentation.state.PaymentEvent
import com.tasneem.safwa.features.payment.presentation.state.PaymentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.tasneem.safwa.features.cart.domain.usecase.GetCartUseCase
import com.tasneem.safwa.features.cart.domain.usecase.ClearCartUseCase

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getSavedCardsUseCase: GetSavedCardsUseCase,
    private val saveCardUseCase: SaveCardUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    private val _effect = Channel<PaymentEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val cartResource = getCartUseCase()
            val checkoutUrl = (cartResource as? com.tasneem.safwa.core.util.Resource.Success)?.data?.checkoutUrl
            
            getSavedCardsUseCase().collect { cards ->
                _state.update { it.copy(savedCards = cards, isLoading = false, checkoutUrl = checkoutUrl) }
            }
        }
    }

    fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(PaymentEffect.NavigateBack) }
            }
            is PaymentEvent.MethodSelected -> {
                _state.update {
                    it.copy(
                        selectedMethod = event.method,
                        selectedCardId = if (event.method == PaymentMethodType.CASH_ON_DELIVERY) null
                        else it.selectedCardId ?: it.savedCards.firstOrNull()?.id
                    )
                }
            }
            is PaymentEvent.CardSelected -> {
                _state.update { it.copy(selectedCardId = event.cardId) }
            }
            is PaymentEvent.ToggleAddCardDialog -> {
                _state.update { it.copy(showAddCardDialog = event.show) }
            }
            is PaymentEvent.SaveNewCard -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    val cardDetails = CardDetails(
                        number = event.number,
                        firstName = event.firstName,
                        lastName = event.lastName,
                        month = event.month,
                        year = event.year,
                        verificationValue = event.verificationValue
                    )
                    saveCardUseCase(cardDetails).collect { result ->
                        result.onSuccess { savedCard ->
                            _state.update { s ->
                                s.copy(
                                    savedCards = s.savedCards + savedCard,
                                    showAddCardDialog = false,
                                    selectedCardId = savedCard.id,
                                    isLoading = false
                                )
                            }
                        }.onFailure {
                            _state.update { s -> s.copy(isLoading = false, errorMessage = it.message) }
                        }
                    }
                }
            }
            is PaymentEvent.ContinueClicked -> {
                when (val currentMethod = _state.value.selectedMethod ?: return) {
                    PaymentMethodType.SHOPIFY -> onEvent(PaymentEvent.ShopifyClicked)
                    PaymentMethodType.CASH_ON_DELIVERY -> navigateToCheckout("cod")
                    PaymentMethodType.PAYMOCK -> navigateToCheckout("paymock")
                    PaymentMethodType.VISA -> {
                        val cardId = _state.value.selectedCardId ?: return
                        navigateToCheckout("card:$cardId")
                    }
                }
            }
            is PaymentEvent.ShopifyClicked -> {
                viewModelScope.launch {
                    val checkoutUrl = _state.value.checkoutUrl
                    if (checkoutUrl != null) {
                        _effect.send(PaymentEffect.LaunchShopifyCheckout(checkoutUrl))
                    } else {
                        _effect.send(PaymentEffect.ShowSnackBar(R.string.unknown_error))
                    }
                }
            }
            is PaymentEvent.ShopifyPaymentCompleted -> {
                viewModelScope.launch {
                    if (event.success) {
                        clearCartUseCase()
                        _state.update { it.copy(isSuccess = true) }
                        _effect.send(PaymentEffect.NavigateToOrderConfirmed(event.orderId, event.totalAmount))
                    } else {
                        _effect.send(PaymentEffect.NavigateToOrderFailed)
                    }
                }
            }
            is PaymentEvent.PayMockClicked -> navigateToCheckout("paymock")
        }
    }

    private fun navigateToCheckout(methodId: String) {
        viewModelScope.launch {
            _effect.send(PaymentEffect.NavigateToCheckout(methodId))
        }
    }
}
