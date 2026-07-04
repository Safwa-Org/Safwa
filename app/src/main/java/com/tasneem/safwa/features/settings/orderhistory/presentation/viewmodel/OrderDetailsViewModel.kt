package com.tasneem.safwa.features.settings.orderhistory.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.usecase.GetOrderByIdUseCase
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // The navigation argument name will be "orderId" as defined in ScreenRoute
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _order = MutableStateFlow<OrderHistoryItem?>(null)
    val order: StateFlow<OrderHistoryItem?> = _order.asStateFlow()

    init {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId).collect { item ->
                _order.update { item }
            }
        }
    }
}
