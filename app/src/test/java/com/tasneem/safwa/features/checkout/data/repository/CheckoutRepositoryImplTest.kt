package com.tasneem.safwa.features.checkout.data.repository

import com.tasneem.network.datasource.checkout.CheckoutRemoteDataSource
import com.tasneem.network.dto.checkout.CompletedDraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderDto
import com.tasneem.network.dto.checkout.DraftOrderInputDto
import com.tasneem.network.dto.checkout.MoneyBagDto
import com.tasneem.network.dto.checkout.MoneyDto
import com.tasneem.network.dto.checkout.OrderRefDto
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.NoInternetException
import com.tasneem.network.exception.UserErrorException
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderLineItem
import com.tasneem.safwa.features.checkout.domain.model.DraftOrderRequest
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CheckoutRepositoryImplTest {

    private val remoteDataSource = mockk<CheckoutRemoteDataSource>()
    private val repository = CheckoutRepositoryImpl(remoteDataSource)

    private val request = DraftOrderRequest(
        email = "buyer@example.com",
        lineItems = listOf(DraftOrderLineItem("gid://variant/1", 1)),
        shippingAddress = Address(id = "gid://address/1", street = "12 Nile St", countryName = "Egypt"),
        discountAmount = null,
        discountCode = null,
        shippingLineTitle = null,
        shippingLineAmount = null,
        note = "note"
    )

    private fun draftOrderDto() = DraftOrderDto(
        id = "gid://draft/1",
        totalPriceSet = MoneyBagDto(MoneyDto("119.0", "EGP"))
    )

    // ---- createDraftOrder ----

    @Test
    fun `createDraftOrder success maps dto to domain`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } returns draftOrderDto()

        val result = repository.createDraftOrder(request)

        result as Resource.Success
        assertEquals("gid://draft/1", result.data.id)
        assertEquals(119.0, result.data.total)
        assertEquals("EGP", result.data.currency)
        coVerify(exactly = 1) { remoteDataSource.createDraftOrder(any<DraftOrderInputDto>()) }
    }

    @Test
    fun `createDraftOrder maps NoInternetException to friendly message`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws NoInternetException()

        val result = repository.createDraftOrder(request)

        assertEquals("No internet connection", (result as Resource.Error).message)
    }

    @Test
    fun `createDraftOrder maps NetworkException to friendly message`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws NetworkException()

        val result = repository.createDraftOrder(request)

        assertEquals("Network error, please try again", (result as Resource.Error).message)
    }

    @Test
    fun `createDraftOrder maps GraphQlException to its errors text`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws GraphQlException("bad field")

        val result = repository.createDraftOrder(request)

        assertEquals("bad field", (result as Resource.Error).message)
    }

    @Test
    fun `createDraftOrder with empty GraphQl errors uses fallback message`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws GraphQlException("")

        val result = repository.createDraftOrder(request)

        assertEquals("Failed to create the order", (result as Resource.Error).message)
    }

    @Test
    fun `createDraftOrder maps UserErrorException to joined messages`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws
            UserErrorException(listOf("Line invalid", "Address missing"))

        val result = repository.createDraftOrder(request)

        assertEquals("Line invalid\nAddress missing", (result as Resource.Error).message)
    }

    @Test
    fun `createDraftOrder maps generic exception to localized message`() = runTest {
        coEvery { remoteDataSource.createDraftOrder(any()) } throws IllegalStateException("kaboom")

        val result = repository.createDraftOrder(request)

        result as Resource.Error
        assertEquals("kaboom", result.message)
        assertTrue(result.throwable is IllegalStateException)
    }

    // ---- completeDraftOrder ----

    @Test
    fun `completeDraftOrder success maps to completed order`() = runTest {
        coEvery { remoteDataSource.completeDraftOrder("gid://draft/1", true) } returns
            CompletedDraftOrderDto(id = "gid://draft/1", order = OrderRefDto("gid://order/9", "#1009"))

        val result = repository.completeDraftOrder("gid://draft/1")

        result as Resource.Success
        assertEquals("gid://order/9", result.data.orderId)
        assertEquals("#1009", result.data.orderName)
    }

    @Test
    fun `completeDraftOrder with null order maps to error via empty response`() = runTest {
        coEvery { remoteDataSource.completeDraftOrder(any(), any()) } returns
            CompletedDraftOrderDto(id = "gid://draft/1", order = null)

        val result = repository.completeDraftOrder("gid://draft/1")

        assertEquals("Failed to create the order", (result as Resource.Error).message)
    }

    // ---- markOrderAsPaid ----

    @Test
    fun `markOrderAsPaid success returns unit`() = runTest {
        coEvery { remoteDataSource.markOrderAsPaid("gid://order/9") } returns Unit

        val result = repository.markOrderAsPaid("gid://order/9")

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { remoteDataSource.markOrderAsPaid("gid://order/9") }
    }

    @Test
    fun `markOrderAsPaid failure uses payment fallback message`() = runTest {
        coEvery { remoteDataSource.markOrderAsPaid(any()) } throws GraphQlException("")

        val result = repository.markOrderAsPaid("gid://order/9")

        assertEquals("Failed to confirm the payment", (result as Resource.Error).message)
    }

    // ---- cancelOrder ----

    @Test
    fun `cancelOrder success forwards notifyCustomer false`() = runTest {
        coEvery { remoteDataSource.cancelOrder("gid://order/9", false) } returns Unit

        val result = repository.cancelOrder("gid://order/9")

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { remoteDataSource.cancelOrder("gid://order/9", false) }
    }

    @Test
    fun `cancelOrder failure uses cancel fallback message`() = runTest {
        coEvery { remoteDataSource.cancelOrder(any(), any()) } throws NetworkException()

        val result = repository.cancelOrder("gid://order/9")

        assertEquals("Network error, please try again", (result as Resource.Error).message)
    }
}
