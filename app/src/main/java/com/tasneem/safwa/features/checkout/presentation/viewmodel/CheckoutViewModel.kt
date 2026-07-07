package com.tasneem.safwa.features.checkout.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tasneem.safwa.R
import com.tasneem.safwa.core.navigation.ScreenRoute
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.usecase.ClearCartUseCase
import com.tasneem.safwa.features.checkout.domain.model.CheckoutData
import com.tasneem.safwa.features.checkout.domain.model.CheckoutValidationError
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.usecase.CalculateOrderSummaryUseCase
import com.tasneem.safwa.features.checkout.domain.usecase.CompleteDraftOrderUseCase
import com.tasneem.safwa.features.checkout.domain.usecase.CreateDraftOrderUseCase
import com.tasneem.safwa.features.checkout.domain.usecase.GetCheckoutDataUseCase
import com.tasneem.safwa.features.checkout.domain.usecase.MarkOrderAsPaidUseCase
import com.tasneem.safwa.features.checkout.domain.usecase.ValidateCheckoutUseCase
import com.tasneem.safwa.features.checkout.presentation.mapper.PaymentMethodIds
import com.tasneem.safwa.features.checkout.presentation.mapper.formatMoney
import com.tasneem.safwa.features.checkout.presentation.mapper.selectedPaymentUiModel
import com.tasneem.safwa.features.checkout.presentation.mapper.toUiModel
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEffect
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEvent
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutState
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutStatus
import com.tasneem.safwa.features.checkout.presentation.state.PlaceOrderStep
import com.tasneem.safwa.features.checkout.presentation.state.RetryAction
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.usecase.ProcessPayMockPaymentUseCase
import com.tasneem.safwa.features.payment.domain.usecase.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCheckoutData: GetCheckoutDataUseCase,
    private val validateCheckout: ValidateCheckoutUseCase,
    private val calculateOrderSummary: CalculateOrderSummaryUseCase,
    private val createDraftOrder: CreateDraftOrderUseCase,
    private val completeDraftOrder: CompleteDraftOrderUseCase,
    private val markOrderAsPaid: MarkOrderAsPaidUseCase,
    private val clearCart: ClearCartUseCase,
    private val processPayment: ProcessPaymentUseCase,
    private val processPayMockPayment: ProcessPayMockPaymentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _effect = Channel<CheckoutEffect>()
    val effect = _effect.receiveAsFlow()

    private var checkoutData: CheckoutData? = null

    private val selectedPaymentId: String =
        savedStateHandle.toRoute<ScreenRoute.Checkout>().methodId

    private var draftOrder: DraftOrder? = null
    private var pendingOrder: CompletedOrder? = null
    private var paymentCaptured = false

    init {
        load()
    }

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            CheckoutEvent.OnBackClick -> send(CheckoutEffect.NavigateBack)
            CheckoutEvent.OnChangeAddressClick ->
                _state.update { it.copy(showAddressPicker = true) }

            CheckoutEvent.OnDismissAddressPicker ->
                _state.update { it.copy(showAddressPicker = false) }

            is CheckoutEvent.OnAddressSelected -> onAddressSelected(event.addressId)
            CheckoutEvent.OnManageAddressesClick -> {
                _state.update { it.copy(showAddressPicker = false) }
                send(CheckoutEffect.NavigateToSavedAddresses)
            }

            CheckoutEvent.OnScreenResumed -> refreshIfReady()
            CheckoutEvent.OnChangePaymentMethodClick -> send(CheckoutEffect.NavigateBack)
            CheckoutEvent.OnPlaceOrderClick -> placeOrder()
            CheckoutEvent.OnRetry -> retry()
            CheckoutEvent.OnErrorDismiss -> dismissError()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(status = CheckoutStatus.Loading) }
            when (val result = getCheckoutData()) {
                is Resource.Success -> bindData(result.data)
                is Resource.Error -> _state.update {
                    it.copy(
                        status = CheckoutStatus.Failure(
                            message = result.message,
                            retry = RetryAction.RELOAD
                        )
                    )
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun refreshIfReady() {
        if (_state.value.status !is CheckoutStatus.Ready) return
        viewModelScope.launch {
            val result = getCheckoutData()
            if (result is Resource.Success) bindData(result.data)
        }
    }

    private fun bindData(data: CheckoutData) {
        checkoutData = data
        val addresses = data.addresses.map { it.toUiModel() }
        val keptSelection = _state.value.selectedAddressId
            ?.takeIf { id -> addresses.any { it.id == id } }

        if (_state.value.selectedAddressId != null && keptSelection == null) invalidateOrder()
        _state.update { current ->
            current.copy(
                status = if (data.cart.lines.isEmpty()) {
                    CheckoutStatus.Failure(
                        messageRes = R.string.checkout_error_empty_cart,
                        retry = RetryAction.NONE
                    )
                } else {
                    CheckoutStatus.Ready
                },
                cartItems = data.cart.lines.map { it.toUiModel() },
                addresses = addresses,
                selectedAddressId = keptSelection ?: addresses.firstOrNull()?.id,
                paymentMethod = selectedPaymentUiModel(selectedPaymentId, data.savedCards),
                summary = calculateOrderSummary(data.cart).toUiModel()
            )
        }
    }

    private fun onAddressSelected(addressId: String) {
        if (_state.value.selectedAddressId != addressId) invalidateOrder()
        _state.update { it.copy(selectedAddressId = addressId, showAddressPicker = false) }
    }

    private fun invalidateOrder() {
        draftOrder = null
        pendingOrder = null
        paymentCaptured = false
    }

    private fun placeOrder() {
        val data = checkoutData ?: return
        val selection = PaymentMethodIds.toSelection(selectedPaymentId)
        val selectedAddress = data.addresses
            .firstOrNull { it.id == _state.value.selectedAddressId }

        validateCheckout(data, selectedAddress, selection).firstOrNull()?.let { error ->
            _state.update { it.copy(status = error.toFailureStatus()) }
            return
        }

        viewModelScope.launch {
            val draft = draftOrder ?: run {
                _state.update {
                    it.copy(status = CheckoutStatus.PlacingOrder(PlaceOrderStep.CREATING_ORDER))
                }
                when (val result = createDraftOrder(
                    cart = data.cart,
                    summary = calculateOrderSummary(data.cart),
                    shippingAddress = selectedAddress!!,
                    customerEmail = data.customerEmail
                )) {
                    is Resource.Success -> result.data.also { draftOrder = it }
                    is Resource.Error -> return@launch fail(result.message, RetryAction.PLACE_ORDER)
                    is Resource.Loading -> return@launch
                }
            }

            val order = pendingOrder ?: run {
                _state.update {
                    it.copy(status = CheckoutStatus.PlacingOrder(PlaceOrderStep.CREATING_ORDER))
                }
                when (val result = completeDraftOrder(draft.id)) {
                    is Resource.Success -> result.data.also { pendingOrder = it }
                    is Resource.Error -> return@launch fail(result.message, RetryAction.PLACE_ORDER)
                    is Resource.Loading -> return@launch
                }
            }

            val payment = selection!!
            if (payment.method == PaymentMethodType.CASH_ON_DELIVERY) {
                onOrderFinished(order, draft)
                return@launch
            }

            if (!paymentCaptured) {
                _state.update {
                    it.copy(status = CheckoutStatus.PlacingOrder(PlaceOrderStep.PROCESSING_PAYMENT))
                }
                val paymentResult = when (payment.method) {
                    PaymentMethodType.VISA -> processPayment(
                        order.orderId,
                        draft.total,
                        PaymentDetails(PaymentMethodType.VISA, payment.cardId)
                    ).awaitResult().map { }

                    PaymentMethodType.PAYMOCK ->
                        processPayMockPayment(draft.total).awaitResult().map { }

                    else -> Result.failure(IllegalStateException("Unsupported method"))
                }
                paymentResult.onFailure { error ->
                    return@launch fail(
                        error.message,
                        RetryAction.PLACE_ORDER,
                        messageRes = R.string.checkout_error_payment_failed
                    )
                }
                paymentCaptured = true
            }

            markPaidAndFinish(order, draft)
        }
    }

    private suspend fun markPaidAndFinish(order: CompletedOrder, draft: DraftOrder) {
        _state.update {
            it.copy(status = CheckoutStatus.PlacingOrder(PlaceOrderStep.CONFIRMING_PAYMENT))
        }
        when (val result = markOrderAsPaid(order.orderId)) {
            is Resource.Success -> onOrderFinished(order, draft)
            is Resource.Error -> fail(result.message, RetryAction.MARK_PAID)
            is Resource.Loading -> Unit
        }
    }

    private suspend fun onOrderFinished(order: CompletedOrder, draft: DraftOrder) {
        clearCart()
        _state.update { it.copy(status = CheckoutStatus.Completed(order.orderName)) }
        _effect.send(
            CheckoutEffect.NavigateToOrderConfirmed(
                orderId = order.orderId.substringAfterLast("/"),
                totalAmount = formatMoney(draft.currency, draft.total)
            )
        )
    }

    private fun fail(
        message: String?,
        retry: RetryAction,
        messageRes: Int? = null
    ) {
        _state.update {
            it.copy(status = CheckoutStatus.Failure(message, messageRes, retry))
        }
    }

    private fun retry() {
        val status = _state.value.status as? CheckoutStatus.Failure ?: return
        when (status.retry) {
            RetryAction.RELOAD -> load()
            RetryAction.PLACE_ORDER -> placeOrder()
            RetryAction.MARK_PAID -> {
                val order = pendingOrder ?: return
                val draft = draftOrder ?: return
                viewModelScope.launch { markPaidAndFinish(order, draft) }
            }

            RetryAction.NONE -> dismissError()
        }
    }

    private fun dismissError() {
        if (_state.value.status is CheckoutStatus.Failure) {
            _state.update { it.copy(status = CheckoutStatus.Ready) }
        }
    }

    private fun CheckoutValidationError.toFailureStatus(): CheckoutStatus.Failure = when (this) {
        CheckoutValidationError.EmptyCart ->
            CheckoutStatus.Failure(messageRes = R.string.checkout_error_empty_cart)

        CheckoutValidationError.MissingAddress ->
            CheckoutStatus.Failure(messageRes = R.string.checkout_error_missing_address)

        CheckoutValidationError.MissingPaymentMethod ->
            CheckoutStatus.Failure(messageRes = R.string.checkout_error_missing_payment)
    }

    private fun send(effect: CheckoutEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

private suspend fun <T> Flow<Result<T>>.awaitResult(): Result<T> {
    var last: Result<T> = Result.failure(IllegalStateException("No result emitted"))
    collect { last = it }
    return last
}
