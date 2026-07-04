package com.tasneem.network.datasource.payment

import com.google.gson.annotations.SerializedName

// ── OAuth token ───────────────────────────────────────────────────────────────

data class PayPalTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// ── Create Order request ──────────────────────────────────────────────────────

data class PayPalCreateOrderRequest(
    val intent: String = "CAPTURE",
    @SerializedName("purchase_units") val purchaseUnits: List<PayPalPurchaseUnit>,
    @SerializedName("payment_source") val paymentSource: PayPalPaymentSource
)

data class PayPalPurchaseUnit(
    val amount: PayPalAmount,
    val description: String = "Safwa Store Order"
)

data class PayPalAmount(
    @SerializedName("currency_code") val currencyCode: String = "USD",
    val value: String
)

data class PayPalPaymentSource(
    val paypal: PayPalSourceDetail
)

data class PayPalSourceDetail(
    @SerializedName("experience_context") val experienceContext: PayPalExperienceContext
)

data class PayPalExperienceContext(
    @SerializedName("payment_method_preference") val paymentMethodPreference: String = "IMMEDIATE_PAYMENT_REQUIRED",
    val locale: String = "en-US",
    @SerializedName("shipping_preference") val shippingPreference: String = "NO_SHIPPING",
    @SerializedName("user_action") val userAction: String = "PAY_NOW",
    @SerializedName("return_url") val returnUrl: String = "safwa://paypal/return",
    @SerializedName("cancel_url") val cancelUrl: String = "safwa://paypal/cancel"
)

// ── Create Order response ─────────────────────────────────────────────────────

data class PayPalOrderResponse(
    val id: String,
    val status: String,
    val links: List<PayPalLink>
) {
    /** Returns the payer-action link (the URL to open in Chrome Custom Tab) */
    fun approvalUrl(): String? = links.firstOrNull {
        it.rel == "payer-action" || it.rel == "approve"
    }?.href
}

data class PayPalLink(
    val href: String,
    val rel: String,
    val method: String
)

// ── Capture response ──────────────────────────────────────────────────────────

data class PayPalCaptureResponse(
    val id: String,
    val status: String   // "COMPLETED", "PENDING", etc.
)
