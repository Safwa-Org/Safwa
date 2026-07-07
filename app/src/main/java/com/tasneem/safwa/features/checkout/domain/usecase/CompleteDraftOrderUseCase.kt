package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class CompleteDraftOrderUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(draftOrderId: String): Resource<CompletedOrder> =
        checkoutRepository.completeDraftOrder(draftOrderId)
}
