package com.tasneem.network.datasource.payment

import android.util.Base64
import com.tasneem.safwa.network.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PayPalRemoteDataSource @Inject constructor(
    private val payPalApiService: PayPalApiService
) : IPayPalRemoteDataSource {

    private val basicAuthHeader: String by lazy {
        val credentials = "${BuildConfig.PAYPAL_CLIENT_ID}:${BuildConfig.PAYPAL_SECRET}"
        "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }

    override suspend fun createOrder(amount: Double): Pair<String, String> =
        withContext(Dispatchers.IO) {
            val tokenBody = "grant_type=client_credentials"
                .toRequestBody("application/x-www-form-urlencoded".toMediaType())
            val tokenResponse = payPalApiService.getAccessToken(basicAuthHeader, tokenBody)
            val bearer = "Bearer ${tokenResponse.accessToken}"

            val orderRequest = PayPalCreateOrderRequest(
                purchaseUnits = listOf(
                    PayPalPurchaseUnit(
                        amount = PayPalAmount(value = String.format("%.2f", amount))
                    )
                ),
                paymentSource = PayPalPaymentSource(
                    paypal = PayPalSourceDetail(
                        experienceContext = PayPalExperienceContext()
                    )
                )
            )
            val orderResponse = payPalApiService.createOrder(bearer, request = orderRequest)
            val approvalUrl = orderResponse.approvalUrl()
                ?: error("No payer-action link returned by PayPal")

            Pair(approvalUrl, orderResponse.id)
        }

    override suspend fun captureOrder(orderId: String): PayPalCaptureResponse =
        withContext(Dispatchers.IO) {
            val tokenBody = "grant_type=client_credentials"
                .toRequestBody("application/x-www-form-urlencoded".toMediaType())
            val tokenResponse = payPalApiService.getAccessToken(basicAuthHeader, tokenBody)
            val bearer = "Bearer ${tokenResponse.accessToken}"

            payPalApiService.captureOrder(bearer, orderId = orderId)
        }
}
