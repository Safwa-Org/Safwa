package com.tasneem.safwa.features.settings.orderhistory.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderHistoryItem
import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import javax.inject.Inject

import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    operator fun invoke(customerAccessToken: String): Flow<Resource<List<OrderHistoryItem>>> {
        return repository.getOrders(customerAccessToken)
    }
}
