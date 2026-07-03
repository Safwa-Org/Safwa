package com.tasneem.safwa.features.settings.orderhistory.presentation

import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderStatus

sealed class OrderHistoryIntent {
    data class LoadOrders(val customerAccessToken: String) : OrderHistoryIntent()
    data class FilterOrders(val status: OrderStatus) : OrderHistoryIntent()
    data class SearchOrders(val query: String) : OrderHistoryIntent()
}
