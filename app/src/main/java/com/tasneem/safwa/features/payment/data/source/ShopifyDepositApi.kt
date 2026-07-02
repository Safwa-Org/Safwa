package com.tasneem.safwa.features.payment.data.source

import retrofit2.http.Body
import retrofit2.http.POST

interface ShopifyDepositApi {
    @POST("sessions")
    suspend fun createSession(@Body request: DepositRequest): DepositResponse
}
