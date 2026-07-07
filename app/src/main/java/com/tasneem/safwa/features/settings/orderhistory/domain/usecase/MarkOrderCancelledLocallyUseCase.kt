package com.tasneem.safwa.features.settings.orderhistory.domain.usecase

import com.tasneem.safwa.features.settings.orderhistory.domain.repository.OrderRepository
import javax.inject.Inject

class MarkOrderCancelledLocallyUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(orderId: String) {
        repository.markOrderCancelled(orderId)
    }
}
