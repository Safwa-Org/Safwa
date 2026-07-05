package com.tasneem.safwa.features.payment.presentation.state

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.shopify.checkoutsheetkit.pixelevents.PixelEvent

class CheckoutEventProcessorImpl(
    activity: ComponentActivity,
    private val onCheckoutCompletedAction: () -> Unit
) : DefaultCheckoutEventProcessor(activity) {

    override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
        onCheckoutCompletedAction()
    }

    override fun onCheckoutFailed(error: CheckoutException) {
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