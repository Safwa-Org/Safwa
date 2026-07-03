package com.tasneem.safwa.features.settings.orderhistory.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderLineItem
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderStatus

@Entity(tableName = "order_history_table")
data class OrderHistoryEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val status: String,
    val lineItems: List<OrderLineItem>,
    val itemCount: Int,
    val date: String,
    val totalPrice: String
)

fun OrderHistoryEntity.toDomain(): OrderHistoryItem {
    return OrderHistoryItem(
        id = this.id,
        orderNumber = this.orderNumber,
        status = runCatching { OrderStatus.valueOf(this.status) }.getOrDefault(OrderStatus.ALL),
        lineItems = this.lineItems,
        itemCount = this.itemCount,
        date = this.date,
        totalPrice = this.totalPrice
    )
}

fun OrderHistoryItem.toEntity(): OrderHistoryEntity {
    return OrderHistoryEntity(
        id = this.id,
        orderNumber = this.orderNumber,
        status = this.status.name,
        lineItems = this.lineItems,
        itemCount = this.itemCount,
        date = this.date,
        totalPrice = this.totalPrice
    )
}
