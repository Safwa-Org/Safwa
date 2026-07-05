package com.tasneem.network.datasource.payment

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.tasneem.safwa.network.type.CurrencyCode
import com.tasneem.safwa.network.type.MailingAddressInput
import com.tasneem.safwa.network.type.MoneyInput
import com.tasneem.safwa.network.type.TokenizedPaymentInputV3
import com.tasneem.network.dto.PaymentRequestDto
import com.tasneem.network.dto.PaymentResponseDto
import com.tasneem.safwa.network.CheckoutCompleteWithTokenizedPaymentV3Mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentRemoteDataSourceImpl @Inject constructor(
    private val depositApi: ShopifyDepositApi,
    private val apolloClient: ApolloClient
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
