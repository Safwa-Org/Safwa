package com.tasneem.network.datasource.payment

import retrofit2.http.Body
import retrofit2.http.POST

interface ShopifyDepositApi {
    @POST("sessions")
    suspend fun createSession(@Body request: DepositRequest): DepositResponse
}
