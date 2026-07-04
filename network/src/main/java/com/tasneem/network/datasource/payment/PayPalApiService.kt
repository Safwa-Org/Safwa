package com.tasneem.network.datasource.payment

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for the PayPal REST API v2.
 * Base URL: https://api-m.sandbox.paypal.com/  (sandbox)
 *           https://api-m.paypal.com/           (production — update PAYPAL_BASE_URL)
 */
interface PayPalApiService {

    /**
     * Exchange client_id + secret for an OAuth2 access token.
     * Authorization header = "Basic <base64(client_id:secret)>"
     * Body = "grant_type=client_credentials" (form-urlencoded)
     */
    @POST("v1/oauth2/token")
    suspend fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Body body: RequestBody
    ): PayPalTokenResponse

    /**
     * Create a PayPal Order (intent = CAPTURE).
     * Returns an order with a payer-action link the user must visit to approve.
     */
    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") bearerToken: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: PayPalCreateOrderRequest
    ): PayPalOrderResponse

    /**
     * Capture an approved PayPal Order.
     * Must be called after the user approves the order in the browser.
     */
    @POST("v2/checkout/orders/{orderId}/capture")
    suspend fun captureOrder(
        @Header("Authorization") bearerToken: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Path("orderId") orderId: String
    ): PayPalCaptureResponse
}
