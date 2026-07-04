package com.tasneem.network.datasource.payment

interface IPayPalRemoteDataSource {

    suspend fun createOrder(amount: Double): Pair<String, String>

    suspend fun captureOrder(orderId: String): PayPalCaptureResponse
}
