package com.tasneem.safwa.features.checkout.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.features.checkout.presentation.state.AddressUiModel
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEffect
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEvent
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutState
import com.tasneem.safwa.features.checkout.presentation.state.PaymentMethodUiModel
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
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        CheckoutState(
            isLoading = false,
            deliveryAddress = AddressUiModel(
                tag = "Home",
                recipientName = "Aisha Al-Marri",
                detailedAddress = "Al Olaya, Riyadh 12241, KSA"
            ),
            paymentMethods = listOf(
                PaymentMethodUiModel(id = "1", title = "Online Payment", subtitle = "Visa • 4242", isSelected = true),
                PaymentMethodUiModel(id = "2", title = "Cash on Delivery", subtitle = "Pay when it arrives", isSelected = false)
            ),
            subtotal = "SAR 1,120",
            shippingFee = "Free",
            isShippingFree = true,
            vatAmount = "SAR 168",
            totalAmount = "SAR 1,238"
        )
    )
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _effect = Channel<CheckoutEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.OnBackClick -> {
                viewModelScope.launch {
                    _effect.send(CheckoutEffect.NavigateBack)
                }
            }

            is CheckoutEvent.OnChangeAddressClick -> {
                viewModelScope.launch {
                    _effect.send(CheckoutEffect.NavigateToSavedAddresses)
                }
            }

            is CheckoutEvent.OnPaymentMethodSelected -> {
                _state.update { currentState ->
                    currentState.copy(
                        paymentMethods = currentState.paymentMethods.map { method ->
                            method.copy(isSelected = method.id == event.methodId)
                        }
                    )
                }
            }

            is CheckoutEvent.OnPlaceOrderClick -> {
                viewModelScope.launch {
                    _effect.send(CheckoutEffect.NavigateToPaymentConfirmation)
                }
            }
        }
    }
}