package com.tasneem.safwa.features.settings.orderhistory.presentation

import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderStatus

data class OrderHistoryState(
    val isLoading: Boolean = false,
    val orders: List<OrderHistoryItem> = emptyList(),
    val filteredOrders: List<OrderHistoryItem> = emptyList(),
    val error: String? = null,
    val selectedFilter: OrderStatus = OrderStatus.ALL,
    val searchQuery: String = ""
)
