package com.tasneem.safwa.features.settings.orderhistory.domain.model

enum class OrderStatus {
    ALL, IN_TRANSIT, DELIVERED, CANCELLED
}

data class OrderLineItem(
    val title: String,
    val quantity: Int,
    val imageUrl: String?
)

data class OrderHistoryItem(
    val id: String,
    val orderNumber: String,
    val status: OrderStatus,
    val lineItems: List<OrderLineItem>,
    val itemCount: Int,
    val date: String,
    val totalPrice: String
)
