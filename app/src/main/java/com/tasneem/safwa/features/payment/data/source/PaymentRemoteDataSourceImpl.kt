package com.tasneem.safwa.features.payment.data.source

import com.tasneem.safwa.features.payment.data.model.PaymentRequestDto
import com.tasneem.safwa.features.payment.data.model.PaymentResponseDto
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import kotlinx.coroutines.delay
import javax.inject.Inject

class PaymentRemoteDataSourceImpl @Inject constructor() : PaymentRemoteDataSource {
    override suspend fun processPayment(request: PaymentRequestDto): PaymentResponseDto {
        // Mocking network delay
        delay(2000)
        return PaymentResponseDto(success = true, transactionId = "TXN_${System.currentTimeMillis()}")
    }

    override suspend fun addCard(cardDetails: CardDetails): Boolean {
        // Mocking network delay
        delay(1000)
        return true
    }
}
