package com.tasneem.safwa.features.settings.orderhistory.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.domain.usecase.CancelOrderUseCase
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.usecase.GetOrderByIdUseCase
import com.tasneem.safwa.features.settings.orderhistory.domain.usecase.MarkOrderCancelledLocallyUseCase
import com.tasneem.safwa.features.settings.orderhistory.presentation.OrderDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val markOrderCancelledLocallyUseCase: MarkOrderCancelledLocallyUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // The navigation argument name will be "orderId" as defined in ScreenRoute
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _order = MutableStateFlow<OrderHistoryItem?>(null)
    val order: StateFlow<OrderHistoryItem?> = _order.asStateFlow()

    private val _uiState = MutableStateFlow(OrderDetailsUiState())
    val uiState: StateFlow<OrderDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId).collect { item ->
                _order.update { item }
            }
        }
    }

    fun onCancelOrderClick() {
        _uiState.update { it.copy(showCancelConfirm = true) }
    }

    fun onDismissCancelConfirm() {
        _uiState.update { it.copy(showCancelConfirm = false) }
    }

    fun onDismissCancelError() {
        _uiState.update { it.copy(cancelError = null) }
    }

    fun onConfirmCancelOrder() {
        _uiState.update {
            it.copy(
                showCancelConfirm = false,
                isCancelling = true,
                cancelError = null
            )
        }
        viewModelScope.launch {
            when (val result = cancelOrderUseCase(orderId)) {
                is Resource.Success -> {
                    markOrderCancelledLocallyUseCase(orderId)
                    _uiState.update { it.copy(isCancelling = false) }
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        isCancelling = false,
                        cancelError = result.message ?: "Failed to cancel the order."
                    )
                }

                is Resource.Loading -> Unit
            }
        }
    }
}
