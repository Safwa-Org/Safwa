package com.tasneem.safwa.features.payment.presentation.state

import android.net.Uri

import androidx.activity.ComponentActivity
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.shopify.checkoutsheetkit.pixelevents.PixelEvent

class CheckoutEventProcessorImpl(
    activity: ComponentActivity,
    private val onCheckoutCompletedAction: (orderId: String, totalAmount: String) -> Unit,
    private val onCheckoutFailedAction: (CheckoutException) -> Unit
) : DefaultCheckoutEventProcessor(activity) {

    override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
        val rawOrderId = checkoutCompletedEvent.orderDetails.id
        val orderId = rawOrderId.substringAfterLast("/")
        val amount = checkoutCompletedEvent.orderDetails.cart.price.total?.amount?.toString() ?: ""
        val currency = checkoutCompletedEvent.orderDetails.cart.price.total?.currencyCode ?: "USD"
        val price = if (amount.isNotBlank()) "$currency $amount" else ""
        onCheckoutCompletedAction(orderId, price)
    }

    override fun onCheckoutFailed(error: CheckoutException) {
        onCheckoutFailedAction(error)
    }

    override fun onCheckoutCanceled() {
    }

    override fun onCheckoutLinkClicked(uri: Uri) {
        super.onCheckoutLinkClicked(uri)
    }

    override fun onWebPixelEvent(event: PixelEvent) {
        super.onWebPixelEvent(event)
    }
}