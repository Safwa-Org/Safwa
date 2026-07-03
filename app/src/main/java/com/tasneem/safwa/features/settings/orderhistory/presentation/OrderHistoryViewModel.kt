package com.tasneem.safwa.features.settings.orderhistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderStatus
import com.tasneem.safwa.features.settings.orderhistory.domain.usecase.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderHistoryState())
    val state: StateFlow<OrderHistoryState> = _state.asStateFlow()

    fun processIntent(intent: OrderHistoryIntent) {
        when (intent) {
            is OrderHistoryIntent.LoadOrders -> loadOrders(intent.customerAccessToken)
            is OrderHistoryIntent.FilterOrders -> filterOrders(intent.status)
            is OrderHistoryIntent.SearchOrders -> searchOrders(intent.query)
        }
    }

    private fun loadOrders(customerAccessToken: String) {
        viewModelScope.launch {
            getOrdersUseCase(customerAccessToken).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orders = result.data ?: emptyList(),
                                filteredOrders = applyFilters(result.data ?: emptyList(), it.selectedFilter, it.searchQuery)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "An unexpected error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun filterOrders(status: OrderStatus) {
        _state.update {
            it.copy(
                selectedFilter = status,
                filteredOrders = applyFilters(it.orders, status, it.searchQuery)
            )
        }
    }

    private fun searchOrders(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredOrders = applyFilters(it.orders, it.selectedFilter, query)
            )
        }
    }

    private fun applyFilters(
        orders: List<com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem>,
        status: OrderStatus,
        query: String
    ): List<com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem> {
        return orders.filter { order ->
            val matchesFilter = status == OrderStatus.ALL || order.status == status
            val matchesSearch = query.isBlank() || order.orderNumber.contains(query, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }
}
