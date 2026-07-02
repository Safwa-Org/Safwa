package com.tasneem.network.datasource.payment

import com.tasneem.network.dto.PaymentRequestDto
import com.tasneem.network.dto.PaymentResponseDto

interface PaymentRemoteDataSource {
    suspend fun processPayment(request: PaymentRequestDto): PaymentResponseDto
    suspend fun addCard(request: DepositRequest): String
}
