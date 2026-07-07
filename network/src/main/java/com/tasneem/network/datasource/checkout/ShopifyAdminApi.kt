package com.tasneem.network.datasource.checkout

import com.tasneem.network.dto.checkout.AdminGraphQLRequest
import com.tasneem.network.dto.checkout.DraftOrderCompleteDataDto
import com.tasneem.network.dto.checkout.DraftOrderCreateDataDto
import com.tasneem.network.dto.checkout.GraphQLResponseDto
import com.tasneem.network.dto.checkout.OrderCancelDataDto
import com.tasneem.network.dto.checkout.OrderMarkAsPaidDataDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ShopifyAdminApi {

    @POST("graphql.json")
    suspend fun draftOrderCreate(
        @Body body: AdminGraphQLRequest
    ): GraphQLResponseDto<DraftOrderCreateDataDto>

    @POST("graphql.json")
    suspend fun draftOrderComplete(
        @Body body: AdminGraphQLRequest
    ): GraphQLResponseDto<DraftOrderCompleteDataDto>

    @POST("graphql.json")
    suspend fun orderMarkAsPaid(
        @Body body: AdminGraphQLRequest
    ): GraphQLResponseDto<OrderMarkAsPaidDataDto>

    @POST("graphql.json")
    suspend fun orderCancel(
        @Body body: AdminGraphQLRequest
    ): GraphQLResponseDto<OrderCancelDataDto>
}
