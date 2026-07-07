package com.tasneem.safwa.features.settings.orderhistory.domain.repository

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem

import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(customerAccessToken: String): Flow<Resource<List<OrderHistoryItem>>>
    fun getOrderById(orderId: String): Flow<OrderHistoryItem?>
    suspend fun clearOrders()
    suspend fun markOrderCancelled(orderId: String)
}
