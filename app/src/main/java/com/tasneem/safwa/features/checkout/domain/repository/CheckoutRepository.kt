package com.tasneem.safwa.features.checkout.domain.repository

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest

interface CheckoutRepository {

    suspend fun createDraftOrder(request: DraftOrderRequest): Resource<DraftOrder>

    suspend fun completeDraftOrder(draftOrderId: String): Resource<CompletedOrder>

    suspend fun markOrderAsPaid(orderId: String): Resource<Unit>

    suspend fun cancelOrder(orderId: String): Resource<Unit>
}
