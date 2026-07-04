package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CapturePayPalPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(paypalOrderId: String): Flow<Result<Unit>> {
        return repository.capturePayPalPayment(paypalOrderId)
    }
}
