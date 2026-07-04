package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InitiatePayPalPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    /** Returns Pair(approvalUrl, paypalOrderId) */
    operator fun invoke(orderId: String, amount: Double): Flow<Result<Pair<String, String>>> {
        return repository.initiatePayPalPayment(orderId, amount)
    }
}
