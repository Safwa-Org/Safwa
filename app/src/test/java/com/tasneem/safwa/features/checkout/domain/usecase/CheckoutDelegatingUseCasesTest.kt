package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.model.CartLine
import com.tasneem.safwa.features.checkout.domain.model.CompletedOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrder
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.checkout.domain.model.OrderSummary
import com.tasneem.safwa.features.checkout.domain.repository.CheckoutRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CheckoutDelegatingUseCasesTest {

    private val repository = mockk<CheckoutRepository>()

    private fun cartLine(variantId: String, quantity: Int) = CartLine(
        id = "line-$variantId",
        variantId = variantId,
        productId = "gid://product/1",
        vendor = "Safwa",
        productTitle = "Shirt",
        variantTitle = "M",
        productHandle = "shirt",
        imageUrl = "",
        price = "10.0",
        totalLinePrice = "10.0",
        currency = "EGP",
        quantity = quantity
    )

    private fun cart(lines: List<CartLine>, shippingTitle: String? = "Express") = Cart(
        id = "gid://cart/1",
        checkoutUrl = "url",
        subtotalAmount = "100.0",
        totalAmount = "119.0",
        currency = "EGP",
        shippingAmount = "5.0",
        shippingTitle = shippingTitle,
        lines = lines
    )

    private fun summary(discount: Double, code: String? = null, shipping: Double? = 5.0) = OrderSummary(
        subtotal = 100.0,
        discount = discount,
        appliedDiscountCode = code,
        shipping = shipping,
        tax = 14.0,
        total = 119.0,
        currency = "EGP"
    )

    private val address = Address(id = "gid://address/1", street = "12 Nile St")

    // ---- CreateDraftOrderUseCase ----

    @Test
    fun `create draft order builds request from cart summary and address`() = runTest {
        val requestSlot = slot<DraftOrderRequest>()
        val expected = Resource.Success(
            DraftOrder(id = "gid://draft/1", subtotal = 100.0, tax = 14.0, shipping = 5.0, total = 119.0, currency = "EGP")
        )
        coEvery { repository.createDraftOrder(capture(requestSlot)) } returns expected

        val result = CreateDraftOrderUseCase(repository)(
            cart = cart(listOf(cartLine("gid://variant/1", 2), cartLine("gid://variant/2", 1))),
            summary = summary(discount = 15.0, code = "SAVE15", shipping = 5.0),
            shippingAddress = address,
            customerEmail = "buyer@example.com"
        )

        assertSame(expected, result)
        val request = requestSlot.captured
        assertEquals("buyer@example.com", request.email)
        assertEquals(listOf("gid://variant/1", "gid://variant/2"), request.lineItems.map { it.variantId })
        assertEquals(listOf(2, 1), request.lineItems.map { it.quantity })
        assertSame(address, request.shippingAddress)
        assertEquals(15.0, request.discountAmount)
        assertEquals("SAVE15", request.discountCode)
        assertEquals("Express", request.shippingLineTitle)
        assertEquals(5.0, request.shippingLineAmount)
        assertEquals("Placed via Safwa Android app", request.note)
    }

    @Test
    fun `create draft order omits discount amount when discount is zero`() = runTest {
        val requestSlot = slot<DraftOrderRequest>()
        coEvery { repository.createDraftOrder(capture(requestSlot)) } returns
            Resource.Success(DraftOrder("gid://draft/1", null, null, null, 119.0, "EGP"))

        CreateDraftOrderUseCase(repository)(
            cart = cart(listOf(cartLine("gid://variant/1", 1))),
            summary = summary(discount = 0.0),
            shippingAddress = address,
            customerEmail = null
        )

        assertNull(requestSlot.captured.discountAmount)
        assertNull(requestSlot.captured.email)
    }

    @Test
    fun `create draft order propagates repository error`() = runTest {
        val error = Resource.Error("boom")
        coEvery { repository.createDraftOrder(any()) } returns error

        val result = CreateDraftOrderUseCase(repository)(
            cart = cart(listOf(cartLine("gid://variant/1", 1))),
            summary = summary(discount = 0.0),
            shippingAddress = address,
            customerEmail = null
        )

        assertSame(error, result)
    }

    // ---- CompleteDraftOrderUseCase ----

    @Test
    fun `complete draft order forwards id and returns repository result`() = runTest {
        val expected = Resource.Success(CompletedOrder("gid://draft/1", "gid://order/9", "#1009"))
        coEvery { repository.completeDraftOrder("gid://draft/1") } returns expected

        val result = CompleteDraftOrderUseCase(repository)("gid://draft/1")

        assertSame(expected, result)
        coVerify(exactly = 1) { repository.completeDraftOrder("gid://draft/1") }
    }

    @Test
    fun `complete draft order propagates error`() = runTest {
        val error = Resource.Error("failed")
        coEvery { repository.completeDraftOrder(any()) } returns error

        val result = CompleteDraftOrderUseCase(repository)("gid://draft/1")

        assertSame(error, result)
    }

    // ---- MarkOrderAsPaidUseCase ----

    @Test
    fun `mark order as paid forwards id and returns success`() = runTest {
        coEvery { repository.markOrderAsPaid("gid://order/9") } returns Resource.Success(Unit)

        val result = MarkOrderAsPaidUseCase(repository)("gid://order/9")

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { repository.markOrderAsPaid("gid://order/9") }
    }

    @Test
    fun `mark order as paid propagates error`() = runTest {
        val error = Resource.Error("failed")
        coEvery { repository.markOrderAsPaid(any()) } returns error

        val result = MarkOrderAsPaidUseCase(repository)("gid://order/9")

        assertSame(error, result)
    }

    // ---- CancelOrderUseCase ----

    @Test
    fun `cancel order forwards id and returns success`() = runTest {
        coEvery { repository.cancelOrder("gid://order/9") } returns Resource.Success(Unit)

        val result = CancelOrderUseCase(repository)("gid://order/9")

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { repository.cancelOrder("gid://order/9") }
    }

    @Test
    fun `cancel order propagates error`() = runTest {
        val error = Resource.Error("failed")
        coEvery { repository.cancelOrder(any()) } returns error

        val result = CancelOrderUseCase(repository)("gid://order/9")

        assertSame(error, result)
    }
}
