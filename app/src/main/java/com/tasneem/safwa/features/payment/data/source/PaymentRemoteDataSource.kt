package com.tasneem.safwa.features.payment.data.source

import com.tasneem.safwa.features.payment.data.model.PaymentRequestDto
import com.tasneem.safwa.features.payment.data.model.PaymentResponseDto

interface PaymentRemoteDataSource {
    suspend fun processPayment(request: PaymentRequestDto): PaymentResponseDto
    suspend fun addCard(request: DepositRequest): String
}
