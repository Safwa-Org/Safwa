package com.tasneem.safwa.features.payment.data.repository

import com.tasneem.safwa.features.payment.data.model.PaymentRequestDto
import com.tasneem.safwa.features.payment.data.source.PaymentRemoteDataSource
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource
) : PaymentRepository {

    // In a real app, this might come from a local database or secure storage
    private val dummyCards = mutableListOf(
        SavedCard("1", "Visa", "4242", "Aisha Al-Marri", "08/28", true),
        SavedCard("2", "Mada", "1187", "Aisha Al-Marri", "11/27", false)
    )

    override fun getSavedCards(): Flow<List<SavedCard>> = flow {
        emit(dummyCards.toList())
    }

    override fun saveNewCard(cardDetails: CardDetails): Flow<Result<SavedCard>> = flow {
        try {
            val success = remoteDataSource.addCard(cardDetails)
            if (success) {
                val newCard = SavedCard(
                    id = System.currentTimeMillis().toString(),
                    type = "Visa",
                    last4 = cardDetails.number.takeLast(4),
                    cardholderName = "${cardDetails.firstName} ${cardDetails.lastName}",
                    expiryDate = "${cardDetails.month}/${cardDetails.year.takeLast(2)}",
                    isDefault = false
                )
                dummyCards.add(newCard)
                emit(Result.success(newCard))
            } else {
                emit(Result.failure(Exception("Failed to add card")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun processPayment(
        orderId: String,
        paymentDetails: PaymentDetails
    ): Flow<Result<Unit>> = flow {
        try {
            val response = remoteDataSource.processPayment(
                PaymentRequestDto(
                    orderId = orderId,
                    paymentMethodId = paymentDetails.cardId
                )
            )
            if (response.success) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception(response.errorMessage ?: "Payment failed")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
