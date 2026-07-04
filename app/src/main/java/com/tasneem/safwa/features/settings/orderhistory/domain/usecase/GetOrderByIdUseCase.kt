package com.tasneem.safwa.features.settings.orderhistory.domain.usecase

import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    operator fun invoke(orderId: String): Flow<OrderHistoryItem?> {
        return repository.getOrderById(orderId)
    }
}
