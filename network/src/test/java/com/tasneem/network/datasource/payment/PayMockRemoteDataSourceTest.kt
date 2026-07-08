package com.tasneem.network.datasource.payment

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PayMockRemoteDataSourceTest {

    private val api = mockk<PayMockApiService>()
    private val dataSource = PayMockRemoteDataSource(api)

    @Test
    fun `createPayment returns success wrapping the api response`() = runTest {
        val response = PayMockPaymentResponse(id = "pay_1", status = "approved")
        coEvery { api.createPayment(any(), any(), any(), any()) } returns response

        val result = dataSource.createPayment(50.0)

        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `createPayment forwards the amount in the request`() = runTest {
        val requestSlot = slot<PayMockCreatePaymentRequest>()
        coEvery { api.createPayment(any(), any(), any(), capture(requestSlot)) } returns
            PayMockPaymentResponse(id = "pay_1", status = "approved")

        dataSource.createPayment(123.45)

        assertEquals(123.45, requestSlot.captured.amount)
    }

    @Test
    fun `createPayment returns failure when the api throws`() = runTest {
        val error = RuntimeException("network down")
        coEvery { api.createPayment(any(), any(), any(), any()) } throws error

        val result = dataSource.createPayment(50.0)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
