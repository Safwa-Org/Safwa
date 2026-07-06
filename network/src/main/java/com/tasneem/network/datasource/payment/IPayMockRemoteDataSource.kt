package com.tasneem.network.datasource.payment

interface IPayMockRemoteDataSource {
    suspend fun createPayment(amount: Double): Result<PayMockPaymentResponse>
}
