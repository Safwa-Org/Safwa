package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import javax.inject.Inject

class MarkOrderAsPaidUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
) {
    suspend operator fun invoke(orderId: String): Resource<Unit> =
        checkoutRepository.markOrderAsPaid(orderId)
}
