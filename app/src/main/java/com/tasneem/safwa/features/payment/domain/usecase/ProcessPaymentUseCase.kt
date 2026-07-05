package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(orderId: String, amount: Double, paymentDetails: PaymentDetails): Flow<Result<Unit>> {
        return repository.processPayment(orderId, amount, paymentDetails)
    }
}
