package com.tasneem.safwa.features.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.usecase.CapturePayPalPaymentUseCase
import com.tasneem.safwa.features.payment.domain.usecase.GetSavedCardsUseCase
import com.tasneem.safwa.features.payment.domain.usecase.InitiatePayPalPaymentUseCase
import com.tasneem.safwa.features.payment.domain.usecase.ProcessPaymentUseCase
import com.tasneem.safwa.features.payment.domain.usecase.SaveCardUseCase
import com.tasneem.safwa.features.cart.domain.usecase.GetCartUseCase
import com.tasneem.safwa.core.util.Resource
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

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getSavedCardsUseCase: GetSavedCardsUseCase,
    private val saveCardUseCase: SaveCardUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val initiatePayPalPaymentUseCase: InitiatePayPalPaymentUseCase,
    private val capturePayPalPaymentUseCase: CapturePayPalPaymentUseCase
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
            getSavedCardsUseCase().collect { cards ->
                _state.update { it.copy(savedCards = cards, isLoading = false) }
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
                val currentMethod = _state.value.selectedMethod ?: return
                when (currentMethod) {
                    PaymentMethodType.PAYPAL -> onEvent(PaymentEvent.PayPalClicked)
                    PaymentMethodType.CASH_ON_DELIVERY,
                    PaymentMethodType.VISA -> {
                        if (currentMethod == PaymentMethodType.CASH_ON_DELIVERY ||
                            (currentMethod == PaymentMethodType.VISA && _state.value.selectedCardId != null)
                        ) {
                            viewModelScope.launch {
                                _state.update { it.copy(isLoading = true) }
                                val cartResource = getCartUseCase()
                                if (cartResource is Resource.Success && cartResource.data != null) {
                                    processPaymentUseCase(
                                        cartResource.data.id,
                                        cartResource.data.totalAmount.toDoubleOrNull() ?: 0.0,
                                        PaymentDetails(currentMethod, _state.value.selectedCardId)
                                    ).collect { result ->
                                        result.onSuccess {
                                            _state.update { it.copy(isLoading = false, isSuccess = true) }
                                            _effect.send(PaymentEffect.ShowSnackBar(R.string.payment_successful))
                                        }.onFailure { err ->
                                            _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                                            _effect.send(PaymentEffect.ShowSnackBar(R.string.payment_failed))
                                        }
                                    }
                                } else {
                                    _state.update { it.copy(isLoading = false, errorMessage = "Cart not found") }
                                    _effect.send(PaymentEffect.ShowSnackBar(R.string.payment_failed))
                                }
                            }
                        }
                    }
                }
            }
            is PaymentEvent.PayPalClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    val cartResource = getCartUseCase()
                    if (cartResource is Resource.Success && cartResource.data != null) {
                        val cart = cartResource.data
                        val totalAmount = cart.totalAmount.toDoubleOrNull() ?: 0.0
                        initiatePayPalPaymentUseCase(cart.id, totalAmount).collect { result ->
                            result.onSuccess { (_, paypalOrderId) ->
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        pendingPayPalOrderId = paypalOrderId,
                                        showPayPalDialog = true
                                    )
                                }
                            }.onFailure { err ->
                                _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                                _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_failed))
                            }
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Cart not found") }
                        _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_failed))
                    }
                }
            }
            is PaymentEvent.PayPalDialogConfirmed -> {
                viewModelScope.launch {
                    val orderId = _state.value.pendingPayPalOrderId
                    if (orderId != null) {
                        _state.update { it.copy(isLoading = true, showPayPalDialog = false) }
                        capturePayPalPaymentUseCase(orderId).collect { result ->
                            result.onSuccess {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        isSuccess = true,
                                        pendingPayPalOrderId = null
                                    )
                                }
                                _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_success))
                                _effect.send(PaymentEffect.NavigateToHome)
                            }.onFailure { err ->
                                _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                                _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_failed))
                            }
                        }
                    }
                }
            }
            is PaymentEvent.PayPalDialogDismissed -> {
                _state.update {
                    it.copy(
                        showPayPalDialog = false,
                        pendingPayPalOrderId = null
                    )
                }
            }
        }
    }
}
