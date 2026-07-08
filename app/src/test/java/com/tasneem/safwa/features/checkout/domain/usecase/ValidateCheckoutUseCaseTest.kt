package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.model.CartLine
import com.tasneem.safwa.features.checkout.domain.model.CheckoutData
import com.tasneem.safwa.features.checkout.domain.model.CheckoutPaymentSelection
import com.tasneem.safwa.features.checkout.domain.model.CheckoutValidationError
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateCheckoutUseCaseTest {

    private val useCase = ValidateCheckoutUseCase()

    private fun cartLine() = CartLine(
        id = "line1",
        variantId = "gid://variant/1",
        productId = "gid://product/1",
        vendor = "Safwa",
        productTitle = "Shirt",
        variantTitle = "M",
        productHandle = "shirt",
        imageUrl = "",
        price = "10.0",
        totalLinePrice = "10.0",
        currency = "EGP",
        quantity = 1
    )

    private fun checkoutData(lines: List<CartLine> = listOf(cartLine())) = CheckoutData(
        cart = Cart(
            id = "gid://cart/1",
            checkoutUrl = "url",
            subtotalAmount = "10.0",
            totalAmount = "10.0",
            currency = "EGP",
            shippingAmount = null,
            lines = lines
        ),
        addresses = emptyList(),
        savedCards = emptyList(),
        customerEmail = "buyer@example.com"
    )

    private val address = Address(id = "gid://address/1", street = "12 Nile St")

    @Test
    fun `valid checkout returns no errors`() {
        val errors = useCase(
            data = checkoutData(),
            selectedAddress = address,
            payment = CheckoutPaymentSelection(PaymentMethodType.CASH_ON_DELIVERY)
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `empty cart adds EmptyCart error`() {
        val errors = useCase(
            data = checkoutData(lines = emptyList()),
            selectedAddress = address,
            payment = CheckoutPaymentSelection(PaymentMethodType.CASH_ON_DELIVERY)
        )

        assertTrue(errors.contains(CheckoutValidationError.EmptyCart))
    }

    @Test
    fun `missing address adds MissingAddress error`() {
        val errors = useCase(
            data = checkoutData(),
            selectedAddress = null,
            payment = CheckoutPaymentSelection(PaymentMethodType.CASH_ON_DELIVERY)
        )

        assertTrue(errors.contains(CheckoutValidationError.MissingAddress))
    }

    @Test
    fun `null payment adds MissingPaymentMethod error`() {
        val errors = useCase(data = checkoutData(), selectedAddress = address, payment = null)

        assertTrue(errors.contains(CheckoutValidationError.MissingPaymentMethod))
    }

    @Test
    fun `visa without card id adds MissingPaymentMethod error`() {
        val errors = useCase(
            data = checkoutData(),
            selectedAddress = address,
            payment = CheckoutPaymentSelection(PaymentMethodType.VISA, cardId = null)
        )

        assertTrue(errors.contains(CheckoutValidationError.MissingPaymentMethod))
    }

    @Test
    fun `visa with card id is valid`() {
        val errors = useCase(
            data = checkoutData(),
            selectedAddress = address,
            payment = CheckoutPaymentSelection(PaymentMethodType.VISA, cardId = "card_1")
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `all failures are accumulated`() {
        val errors = useCase(
            data = checkoutData(lines = emptyList()),
            selectedAddress = null,
            payment = null
        )

        assertEquals(
            listOf(
                CheckoutValidationError.EmptyCart,
                CheckoutValidationError.MissingAddress,
                CheckoutValidationError.MissingPaymentMethod
            ),
            errors
        )
    }
}
