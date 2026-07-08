package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.model.DiscountCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CalculateOrderSummaryUseCaseTest {

    private val useCase = CalculateOrderSummaryUseCase()

    private fun cart(
        subtotalAmount: String = "100.0",
        totalAmount: String = "119.0",
        currency: String = "EGP",
        shippingAmount: String? = "5.0",
        taxAmount: String? = "14.0",
        discountCodes: List<DiscountCode> = emptyList()
    ) = Cart(
        id = "gid://cart/1",
        checkoutUrl = "https://shop/checkout",
        subtotalAmount = subtotalAmount,
        totalAmount = totalAmount,
        currency = currency,
        shippingAmount = shippingAmount,
        taxAmount = taxAmount,
        discountCodes = discountCodes,
        lines = emptyList()
    )

    @Test
    fun `parses amounts and passes currency through`() {
        val summary = useCase(cart(currency = "USD"))

        assertEquals(100.0, summary.subtotal)
        assertEquals(119.0, summary.total)
        assertEquals(14.0, summary.tax)
        assertEquals(5.0, summary.shipping)
        assertEquals("USD", summary.currency)
    }

    @Test
    fun `null tax and shipping stay null and contribute zero to discount`() {
        // subtotal 100, total 100 -> discount = 100 - 100 + 0 + 0 = 0
        val summary = useCase(
            cart(subtotalAmount = "100.0", totalAmount = "100.0", taxAmount = null, shippingAmount = null)
        )

        assertNull(summary.tax)
        assertNull(summary.shipping)
        assertEquals(0.0, summary.discount)
    }

    @Test
    fun `unparseable subtotal and total coerce to zero`() {
        val summary = useCase(cart(subtotalAmount = "abc", totalAmount = "xyz"))

        assertEquals(0.0, summary.subtotal)
        assertEquals(0.0, summary.total)
    }

    @Test
    fun `discount is computed from subtotal total tax and shipping`() {
        // 100 - 80 + 14 + 5 = 39
        val summary = useCase(
            cart(subtotalAmount = "100.0", totalAmount = "80.0", taxAmount = "14.0", shippingAmount = "5.0")
        )

        assertEquals(39.0, summary.discount)
    }

    @Test
    fun `negative discount is coerced to zero`() {
        // 100 - 200 + 0 + 0 = -100 -> 0
        val summary = useCase(
            cart(subtotalAmount = "100.0", totalAmount = "200.0", taxAmount = null, shippingAmount = null)
        )

        assertEquals(0.0, summary.discount)
    }

    @Test
    fun `applied discount code is the first applicable code`() {
        val summary = useCase(
            cart(
                discountCodes = listOf(
                    DiscountCode(code = "EXPIRED", applicable = false),
                    DiscountCode(code = "SAVE10", applicable = true),
                    DiscountCode(code = "SAVE20", applicable = true)
                )
            )
        )

        assertEquals("SAVE10", summary.appliedDiscountCode)
    }

    @Test
    fun `no applicable discount code yields null`() {
        val summary = useCase(
            cart(discountCodes = listOf(DiscountCode(code = "EXPIRED", applicable = false)))
        )

        assertNull(summary.appliedDiscountCode)
    }
}
