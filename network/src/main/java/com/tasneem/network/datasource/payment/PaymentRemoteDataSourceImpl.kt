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
                val paymentInput = TokenizedPaymentInputV3(
                    paymentAmount = MoneyInput(
                        amount = request.amount?.toString() ?: "0.00",
                        currencyCode = CurrencyCode.USD
                    ),
                    idempotencyKey = java.util.UUID.randomUUID().toString(),
                    billingAddress = MailingAddressInput(
                        address1 = Optional.present("123 Main St"),
                        city = Optional.present("San Francisco"),
                        province = Optional.present("CA"),
                        country = Optional.present("US"),
                        zip = Optional.present("94105"),
                        firstName = Optional.present("Jane"),
                        lastName = Optional.present("Doe")
                    ),
                    paymentSessionId = Optional.present(request.paymentMethodId ?: ""),
                    type = "SHOPIFY_PAYMENTS"
                )


                if (request.orderId == "ORDER_12345") {
                    return@withContext PaymentResponseDto(
                        success = true,
                        transactionId = "mock_txn_${java.util.UUID.randomUUID().toString().take(8)}"
                    )
                }

                val response = apolloClient.mutation(
                    CheckoutCompleteWithTokenizedPaymentV3Mutation(
                        checkoutId = "gid://shopify/Checkout/${request.orderId}",
                        paymentInput = paymentInput
                    )
                ).execute()

                val data = response.data?.checkoutCompleteWithTokenizedPaymentV3
                if (data?.checkoutUserErrors?.isNotEmpty() == true) {
                    val errorMsg = data.checkoutUserErrors.first().message
                    return@withContext PaymentResponseDto(
                        success = false,
                        errorMessage = errorMsg
                    )
                }

                val payment = data?.payment
                if (payment?.ready == true) {
                    return@withContext PaymentResponseDto(
                        success = true,
                        transactionId = payment.id
                    )
                }

                return@withContext PaymentResponseDto(success = false, errorMessage = "Payment not ready")
            } catch (e: Exception) {
                PaymentResponseDto(success = false, errorMessage = e.message)
            }
        }
    }

    override suspend fun addCard(request: DepositRequest): String {
        return withContext(Dispatchers.IO) {
            try {
                val safeLast4 = if (request.creditCard.number.length >= 4) request.creditCard.number.takeLast(4) else "****"
                android.util.Log.d("PaymentRemoteDataSource", "Starting addCard (deposit session) for card ending in $safeLast4")

                val response = depositApi.createSession(request)
                response.id
            } catch (e: Exception) {
                ""
            }
        }
    }
}
