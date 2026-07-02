package com.tasneem.safwa.features.payment.data.source

import com.tasneem.safwa.features.payment.data.model.PaymentRequestDto
import com.tasneem.safwa.features.payment.data.model.PaymentResponseDto
import com.tasneem.safwa.features.payment.domain.model.CardDetails

interface PaymentRemoteDataSource {
    suspend fun processPayment(request: PaymentRequestDto): PaymentResponseDto
    suspend fun addCard(cardDetails: CardDetails): Boolean
}
