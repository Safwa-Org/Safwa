package com.tasneem.network.datasource.checkout

import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderInputDto

interface CheckoutRemoteDataSource {

    suspend fun createDraftOrder(input: DraftOrderInputDto): DraftOrderDto

    suspend fun completeDraftOrder(draftOrderId: String, paymentPending: Boolean): CompletedDraftOrderDto

    suspend fun markOrderAsPaid(orderId: String)

    suspend fun cancelOrder(orderId: String, notifyCustomer: Boolean)
}
