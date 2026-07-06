package com.tasneem.network.datasource.payment

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PayMockApiService {
    @POST("api/v1/payments")
    suspend fun createPayment(
        @Header("Authorization") bearerToken: String = "Bearer sk_test_xxx",
        @Header("Content-Type") contentType: String = "application/json",
        @Header("X-PayMock-Rule") rule: String = "LUCKY_777",
        @Body request: PayMockCreatePaymentRequest
    ): PayMockPaymentResponse
}

data class PayMockCreatePaymentRequest(
    val amount: Double,
    val currency: String = "BRL",
    val method: String = "credit_card"
)

data class PayMockPaymentResponse(
    val id: String?,
    val status: String?
)
