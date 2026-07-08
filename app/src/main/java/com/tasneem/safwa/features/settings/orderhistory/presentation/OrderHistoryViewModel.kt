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
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import javax.inject.Inject
import com.tasneem.safwa.R

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderHistoryState())
    val state: StateFlow<OrderHistoryState> = _state.asStateFlow()

    init {
        loadUserAndOrders()
    }

    fun processIntent(intent: OrderHistoryIntent) {
        when (intent) {
            is OrderHistoryIntent.LoadOrders -> loadOrders(intent.customerAccessToken)
            is OrderHistoryIntent.FilterOrders -> filterOrders(intent.status)
            is OrderHistoryIntent.SearchOrders -> searchOrders(intent.query)
        }
    }

    private fun loadUserAndOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getCurrentUserUseCase()
            if (result is Resource.Success && result.data != null) {
                val user = result.data
                val token = user.customerAccessToken
                if (!token.isNullOrEmpty()) {
                    loadOrders(token)
                } else {
                    _state.update { it.copy(isLoading = false, errorResId = R.string.please_login_again_to_view_orders) }
                }
            } else {
                _state.update { it.copy(isLoading = false, errorResId = R.string.user_not_found) }
            }
        }
    }

    private fun loadOrders(customerAccessToken: String) {
        viewModelScope.launch {
            getOrdersUseCase(customerAccessToken).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null, errorResId = null) }
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
                                error = result.message,
                                errorResId = if (result.message == null) R.string.an_unexpected_error_occurred else null
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
