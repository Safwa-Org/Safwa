package com.tasneem.network.datasource.checkout

import com.tasneem.network.dto.checkout.DraftOrderCompleteDataDto
import com.tasneem.network.dto.checkout.DraftOrderCreateDataDto
import com.tasneem.network.dto.checkout.GraphQLResponseDto
import com.tasneem.network.dto.checkout.OrderCancelDataDto
import com.tasneem.network.dto.checkout.OrderMarkAsPaidDataDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AdminProxyApi {

    @POST("draftOrderCreate")
    suspend fun draftOrderCreate(
        @Header("Authorization") authorization: String,
        @Header("X-Firebase-AppCheck") appCheckToken: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): GraphQLResponseDto<DraftOrderCreateDataDto>

    @POST("draftOrderComplete")
    suspend fun draftOrderComplete(
        @Header("Authorization") authorization: String,
        @Header("X-Firebase-AppCheck") appCheckToken: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): GraphQLResponseDto<DraftOrderCompleteDataDto>

    @POST("orderMarkAsPaid")
    suspend fun orderMarkAsPaid(
        @Header("Authorization") authorization: String,
        @Header("X-Firebase-AppCheck") appCheckToken: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): GraphQLResponseDto<OrderMarkAsPaidDataDto>

    @POST("orderCancel")
    suspend fun orderCancel(
        @Header("Authorization") authorization: String,
        @Header("X-Firebase-AppCheck") appCheckToken: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): GraphQLResponseDto<OrderCancelDataDto>
}
