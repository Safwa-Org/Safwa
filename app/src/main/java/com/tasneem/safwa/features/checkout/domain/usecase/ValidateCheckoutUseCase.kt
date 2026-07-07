package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.features.checkout.domain.model.CheckoutData
import com.tasneem.safwa.features.checkout.domain.model.CheckoutPaymentSelection
import com.tasneem.safwa.features.checkout.domain.model.CheckoutValidationError
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import javax.inject.Inject

class ValidateCheckoutUseCase @Inject constructor() {

    operator fun invoke(
        data: CheckoutData,
        selectedAddress: Address?,
        payment: CheckoutPaymentSelection?
    ): List<CheckoutValidationError> = buildList {
        if (data.cart.lines.isEmpty()) add(CheckoutValidationError.EmptyCart)
        if (selectedAddress == null) add(CheckoutValidationError.MissingAddress)
        when {
            payment == null -> add(CheckoutValidationError.MissingPaymentMethod)
            payment.method == PaymentMethodType.VISA && payment.cardId == null ->
                add(CheckoutValidationError.MissingPaymentMethod)
        }
    }
}
