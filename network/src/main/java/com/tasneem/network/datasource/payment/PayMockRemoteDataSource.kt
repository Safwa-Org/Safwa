package com.tasneem.network.datasource.payment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PayMockRemoteDataSource @Inject constructor(
    private val payMockApiService: PayMockApiService
) : IPayMockRemoteDataSource {

    override suspend fun createPayment(amount: Double): Result<PayMockPaymentResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = PayMockCreatePaymentRequest(amount = amount)
                val response = payMockApiService.createPayment(request = request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
