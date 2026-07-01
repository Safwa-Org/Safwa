package com.tasneem.safwa.features.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.features.payment.presentation.state.PaymentEffect
import com.tasneem.safwa.features.payment.presentation.state.PaymentEvent
import com.tasneem.safwa.features.payment.presentation.state.PaymentState
import com.tasneem.safwa.features.payment.presentation.state.PaymentMethodType
import com.tasneem.safwa.features.payment.presentation.state.SavedCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    private val _effect = Channel<PaymentEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val dummyCards = listOf(
            SavedCard("1", "Visa", "4242", "Aisha Al-Marri", "08/28", true),
            SavedCard("2", "Mada", "1187", "Aisha Al-Marri", "11/27", false)
        )
        _state.update { 
            it.copy(
                savedCards = dummyCards
            )
        }
    }

    fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(PaymentEffect.NavigateBack)
                }
            }
            is PaymentEvent.MethodSelected -> {
                _state.update { 
                    it.copy(
                        selectedMethod = event.method,
                        selectedCardId = if (event.method == PaymentMethodType.CASH_ON_DELIVERY) null else it.selectedCardId ?: it.savedCards.firstOrNull()?.id
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
                _state.update { currentState ->
                    val newCard = SavedCard(
                        id = System.currentTimeMillis().toString(),
                        type = "Visa",
                        last4 = event.number.takeLast(4),
                        cardholderName = "${event.firstName} ${event.lastName}",
                        expiryDate = "${event.month}/${event.year.takeLast(2)}",
                        isDefault = false
                    )
                    currentState.copy(
                        savedCards = currentState.savedCards + newCard,
                        showAddCardDialog = false,
                        selectedCardId = newCard.id
                    )
                }
            }
            is PaymentEvent.ContinueClicked -> {
                val currentMethod = _state.value.selectedMethod
                if (currentMethod == PaymentMethodType.CASH_ON_DELIVERY || (currentMethod == PaymentMethodType.VISA && _state.value.selectedCardId != null)) {
                    _state.update { it.copy(isLoading = true) }
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000.milliseconds)
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                        _effect.send(PaymentEffect.ShowSnackBar(R.string.payment_successful))
                        _effect.send(PaymentEffect.NavigateToHome)
                    }
                }
            }
        }
    }
}
