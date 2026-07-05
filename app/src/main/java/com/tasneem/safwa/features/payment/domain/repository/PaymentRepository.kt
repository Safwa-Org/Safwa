package com.tasneem.safwa.features.payment.domain.repository

import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getSavedCards(): Flow<List<SavedCard>>
    fun saveNewCard(cardDetails: CardDetails): Flow<Result<SavedCard>>
    fun processPayment(orderId: String, amount: Double, paymentDetails: PaymentDetails): Flow<Result<Unit>>
}
