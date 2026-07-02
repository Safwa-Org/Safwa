package com.tasneem.safwa.features.payment.data.repository

import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardDao
import com.tasneem.safwa.features.payment.data.datasource.local.toEntity
import com.tasneem.safwa.features.payment.data.model.PaymentRequestDto
import com.tasneem.safwa.features.payment.data.source.PaymentRemoteDataSource
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val savedCardDao: SavedCardDao
) : PaymentRepository {

    override fun getSavedCards(): Flow<List<SavedCard>> {
        return savedCardDao.getSavedCards().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun saveNewCard(cardDetails: CardDetails): Flow<Result<SavedCard>> = flow {
        try {
            val request = com.tasneem.safwa.features.payment.data.source.DepositRequest(
                creditCard = com.tasneem.safwa.features.payment.data.source.DepositCreditCard(
                    number = cardDetails.number.replace(" ", ""),
                    firstName = cardDetails.firstName,
                    lastName = cardDetails.lastName,
                    month = cardDetails.month.toIntOrNull() ?: 12,
                    year = cardDetails.year.toIntOrNull() ?: 2030,
                    verificationValue = cardDetails.verificationValue
                )
            )
            val sessionId = remoteDataSource.addCard(request)
            if (sessionId.isNotEmpty()) {
                val newCard = SavedCard(
                    id = sessionId,
                    type = "Visa",
                    last4 = cardDetails.number.takeLast(4),
                    cardholderName = "${cardDetails.firstName} ${cardDetails.lastName}",
                    expiryDate = "${cardDetails.month}/${cardDetails.year.takeLast(2)}",
                    isDefault = false
                )
                // Persist the new card in the database
                savedCardDao.insertCard(newCard.toEntity())
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
