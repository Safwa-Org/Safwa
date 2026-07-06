package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessPayMockPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(amount: Double): Flow<Result<String>> {
        return repository.processPayMockPayment(amount)
    }
}
