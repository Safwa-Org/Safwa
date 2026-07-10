package com.tasneem.network.datasource.payment

import com.tasneem.network.dto.PaymentRequestDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaymentRemoteDataSourceImplTest {

    private val depositApi = mockk<ShopifyDepositApi>()
    private val dataSource = PaymentRemoteDataSourceImpl(depositApi)

    // ---------- processPayment (mocked success path) ----------

    @Test
    fun `processPayment returns a successful mock response`() = runTest {
        val response = dataSource.processPayment(
            PaymentRequestDto(orderId = "gid://order/1", paymentMethodId = "card_1", amount = 100.0)
        )

        assertTrue(response.success)
        assertTrue(response.transactionId?.startsWith("mock_txn_") == true)
    }

    // ---------- addCard ----------

    private fun depositRequest() = DepositRequest(
        creditCard = DepositCreditCard(
            number = "4242424242424242",
            firstName = "Tasneem",
            lastName = "Ali",
            month = 12,
            year = 2030,
            verificationValue = "123"
        )
    )

    @Test
    fun `addCard returns the session id from the deposit api`() = runTest {
        coEvery { depositApi.createSession(any()) } returns DepositResponse(id = "sess_123")

        val result = dataSource.addCard(depositRequest())

        assertEquals("sess_123", result)
        coVerify(exactly = 1) { depositApi.createSession(any()) }
    }

    @Test
    fun `addCard returns empty string when the deposit api throws`() = runTest {
        coEvery { depositApi.createSession(any()) } throws RuntimeException("boom")

        val result = dataSource.addCard(depositRequest())

        assertEquals("", result)
    }
}
