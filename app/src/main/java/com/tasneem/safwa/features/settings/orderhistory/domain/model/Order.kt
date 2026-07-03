package com.tasneem.safwa.features.settings.orderhistory.domain.model

enum class OrderStatus {
    ALL, IN_TRANSIT, DELIVERED, CANCELLED
}

data class OrderHistoryItem(
    val id: String,
    val orderNumber: String,
    val status: OrderStatus,
    val images: List<String>,
    val itemCount: Int,
    val date: String,
    val totalPrice: String
)
