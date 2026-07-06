package com.tasneem.network.datasource.payment


import com.tasneem.network.dto.PaymentRequestDto
import com.tasneem.network.dto.PaymentResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentRemoteDataSourceImpl @Inject constructor(
    private val depositApi: ShopifyDepositApi,
) : PaymentRemoteDataSource {

    override suspend fun processPayment(request: PaymentRequestDto): PaymentResponseDto {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext PaymentResponseDto(
                    success = true,
                    transactionId = "mock_txn_${java.util.UUID.randomUUID().toString().take(8)}"
                )
            } catch (e: Exception) {
                android.util.Log.e("PaymentRemoteDS", "Exception processing payment", e)
                PaymentResponseDto(success = false, errorMessage = e.message)
            }
        }
    }

    override suspend fun addCard(request: DepositRequest): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = depositApi.createSession(request)
                response.id
            } catch (e: Exception) {
                ""
            }
        }
    }
}
