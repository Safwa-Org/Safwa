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
                                processPaymentUseCase(
                                    "ORDER_12345",
                                    PaymentDetails(currentMethod, _state.value.selectedCardId)
                                ).collect { result ->
                                    result.onSuccess {
                                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                                        _effect.send(PaymentEffect.ShowSnackBar(R.string.payment_successful))
                                        _effect.send(PaymentEffect.NavigateToHome)
                                    }.onFailure { err ->
                                        _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is PaymentEvent.PayPalClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    initiatePayPalPaymentUseCase("ORDER_12345", 10.0).collect { result ->
                        result.onSuccess { (approvalUrl, paypalOrderId) ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    pendingPayPalOrderId = paypalOrderId
                                )
                            }
                            _effect.send(PaymentEffect.LaunchPayPalUrl(approvalUrl))
                        }.onFailure { err ->
                            _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                            _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_failed))
                        }
                    }
                }
            }
            is PaymentEvent.PayPalPaymentCompleted -> {
                viewModelScope.launch {
                    if (event.success) {
                        val orderId = _state.value.pendingPayPalOrderId
                        if (orderId != null) {
                            _state.update { it.copy(isLoading = true) }
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
                        } else {
                            _state.update { it.copy(isSuccess = true) }
                            _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_success))
                            _effect.send(PaymentEffect.NavigateToHome)
                        }
                    } else {
                        _state.update { it.copy(pendingPayPalOrderId = null) }
                        _effect.send(PaymentEffect.ShowSnackBar(R.string.paypal_failed))
                    }
                }
            }
        }
    }
}
