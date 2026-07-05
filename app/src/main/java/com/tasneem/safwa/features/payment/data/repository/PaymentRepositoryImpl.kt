package com.tasneem.safwa.features.payment.data.repository

import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardDao
import com.tasneem.safwa.features.payment.data.datasource.local.toEntity
import com.tasneem.network.dto.PaymentRequestDto
import com.tasneem.network.datasource.payment.PaymentRemoteDataSource
import com.tasneem.network.datasource.payment.IPayPalRemoteDataSource
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
    private val payPalRemoteDataSource: IPayPalRemoteDataSource,
    private val savedCardDao: SavedCardDao
) : PaymentRepository {

    override fun getSavedCards(): Flow<List<SavedCard>> {
        return savedCardDao.getSavedCards().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun saveNewCard(cardDetails: CardDetails): Flow<Result<SavedCard>> = flow {
        try {
            val request = com.tasneem.network.datasource.payment.DepositRequest(
                creditCard = com.tasneem.network.datasource.payment.DepositCreditCard(
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
        amount: Double,
        paymentDetails: PaymentDetails
    ): Flow<Result<Unit>> = flow {
        try {
            val response = remoteDataSource.processPayment(
                PaymentRequestDto(
                    orderId = orderId,
                    paymentMethodId = paymentDetails.cardId,
                    amount = amount
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

    override fun initiatePayPalPayment(
        orderId: String,
        amount: Double
    ): Flow<Result<Pair<String, String>>> = flow {
        try {
            val (approvalUrl, paypalOrderId) = payPalRemoteDataSource.createOrder(amount)
            emit(Result.success(Pair(approvalUrl, paypalOrderId)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun capturePayPalPayment(paypalOrderId: String): Flow<Result<Unit>> = flow {
        try {
            val response = payPalRemoteDataSource.captureOrder(paypalOrderId)
            if (response.status == "COMPLETED") {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Capture status: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
