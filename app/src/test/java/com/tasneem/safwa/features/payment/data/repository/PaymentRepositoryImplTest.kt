package com.tasneem.safwa.features.payment.data.repository

import app.cash.turbine.test
import com.tasneem.network.datasource.payment.DepositRequest
import com.tasneem.network.datasource.payment.IPayMockRemoteDataSource
import com.tasneem.network.datasource.payment.PayMockPaymentResponse
import com.tasneem.network.datasource.payment.PaymentRemoteDataSource
import com.tasneem.network.dto.PaymentRequestDto
import com.tasneem.network.dto.PaymentResponseDto
import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardDao
import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardEntity
import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaymentRepositoryImplTest {

    private val remoteDataSource = mockk<PaymentRemoteDataSource>()
    private val payMockRemoteDataSource = mockk<IPayMockRemoteDataSource>()
    private val savedCardDao = mockk<SavedCardDao>(relaxed = true)

    private val repository = PaymentRepositoryImpl(remoteDataSource, payMockRemoteDataSource, savedCardDao)

    private fun cardDetails(
        number: String = "4242 4242 4242 4242",
        month: String = "9",
        year: String = "2030"
    ) = CardDetails(
        number = number,
        firstName = "Tasneem",
        lastName = "Ali",
        month = month,
        year = year,
        verificationValue = "123"
    )

    // ---------- getSavedCards ----------

    @Test
    fun `getSavedCards maps entities to domain models`() = runTest {
        val entity = SavedCardEntity(
            id = "c1", type = "Visa", last4 = "4242",
            cardholderName = "Tasneem Ali", expiryDate = "9/30", isDefault = true
        )
        every { savedCardDao.getSavedCards() } returns flowOf(listOf(entity))

        repository.getSavedCards().test {
            val cards = awaitItem()
            assertEquals(1, cards.size)
            assertEquals("c1", cards[0].id)
            assertEquals("4242", cards[0].last4)
            assertTrue(cards[0].isDefault)
            awaitComplete()
        }
    }

    // ---------- saveNewCard ----------

    @Test
    fun `saveNewCard on success strips spaces builds card and persists it`() = runTest {
        val requestSlot = slot<DepositRequest>()
        coEvery { remoteDataSource.addCard(capture(requestSlot)) } returns "sess_123"

        repository.saveNewCard(cardDetails(number = "4242 4242 4242 4242", month = "9", year = "2030")).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val card = result.getOrThrow()
            assertEquals("sess_123", card.id)
            assertEquals("Visa", card.type)
            assertEquals("4242", card.last4)
            assertEquals("Tasneem Ali", card.cardholderName)
            assertEquals("9/30", card.expiryDate) // month / last-2 of year
            assertFalse(card.isDefault)
            awaitComplete()
        }
        // card number spaces removed before sending
        assertEquals("4242424242424242", requestSlot.captured.creditCard.number)
        assertEquals(9, requestSlot.captured.creditCard.month)
        assertEquals(2030, requestSlot.captured.creditCard.year)
        coVerify(exactly = 1) { savedCardDao.insertCard(any()) }
    }

    @Test
    fun `saveNewCard falls back to default month and year when not numeric`() = runTest {
        val requestSlot = slot<DepositRequest>()
        coEvery { remoteDataSource.addCard(capture(requestSlot)) } returns "sess_123"

        repository.saveNewCard(cardDetails(month = "ab", year = "xy")).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(12, requestSlot.captured.creditCard.month)
        assertEquals(2030, requestSlot.captured.creditCard.year)
    }

    @Test
    fun `saveNewCard emits failure and does not persist when session id is empty`() = runTest {
        coEvery { remoteDataSource.addCard(any()) } returns ""

        repository.saveNewCard(cardDetails()).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Failed to add card", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify(exactly = 0) { savedCardDao.insertCard(any()) }
    }

    @Test
    fun `saveNewCard emits failure when remote throws`() = runTest {
        val error = RuntimeException("deposit failed")
        coEvery { remoteDataSource.addCard(any()) } throws error

        repository.saveNewCard(cardDetails()).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            awaitComplete()
        }
    }

    // ---------- processPayment ----------

    private val paymentDetails = PaymentDetails(method = PaymentMethodType.VISA, cardId = "card_1")

    @Test
    fun `processPayment emits success when response is successful`() = runTest {
        coEvery { remoteDataSource.processPayment(any()) } returns PaymentResponseDto(success = true, transactionId = "txn_1")

        repository.processPayment("gid://order/1", 100.0, paymentDetails).test {
            assertTrue(awaitItem().isSuccess)
            awaitComplete()
        }
    }

    @Test
    fun `processPayment forwards order id card id and amount`() = runTest {
        val requestSlot = slot<PaymentRequestDto>()
        coEvery { remoteDataSource.processPayment(capture(requestSlot)) } returns PaymentResponseDto(success = true)

        repository.processPayment("gid://order/1", 100.0, paymentDetails).test {
            awaitItem(); awaitComplete()
        }

        assertEquals("gid://order/1", requestSlot.captured.orderId)
        assertEquals("card_1", requestSlot.captured.paymentMethodId)
        assertEquals(100.0, requestSlot.captured.amount)
    }

    @Test
    fun `processPayment emits failure with error message when unsuccessful`() = runTest {
        coEvery { remoteDataSource.processPayment(any()) } returns
            PaymentResponseDto(success = false, errorMessage = "Card declined")

        repository.processPayment("gid://order/1", 100.0, paymentDetails).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Card declined", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `processPayment emits failure with default message when error message is null`() = runTest {
        coEvery { remoteDataSource.processPayment(any()) } returns PaymentResponseDto(success = false, errorMessage = null)

        repository.processPayment("gid://order/1", 100.0, paymentDetails).test {
            assertEquals("Payment failed", awaitItem().exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `processPayment emits failure when remote throws`() = runTest {
        val error = RuntimeException("network")
        coEvery { remoteDataSource.processPayment(any()) } throws error

        repository.processPayment("gid://order/1", 100.0, paymentDetails).test {
            assertEquals(error, awaitItem().exceptionOrNull())
            awaitComplete()
        }
    }

    // ---------- processPayMockPayment ----------

    @Test
    fun `processPayMockPayment emits success with id when approved`() = runTest {
        coEvery { payMockRemoteDataSource.createPayment(50.0) } returns
            Result.success(PayMockPaymentResponse(id = "pay_1", status = "approved"))

        repository.processPayMockPayment(50.0).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("pay_1", result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `processPayMockPayment falls back to mock_id when approved but id is null`() = runTest {
        coEvery { payMockRemoteDataSource.createPayment(any()) } returns
            Result.success(PayMockPaymentResponse(id = null, status = "approved"))

        repository.processPayMockPayment(50.0).test {
            assertEquals("mock_id", awaitItem().getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `processPayMockPayment emits failure with status when not approved`() = runTest {
        coEvery { payMockRemoteDataSource.createPayment(any()) } returns
            Result.success(PayMockPaymentResponse(id = "pay_1", status = "declined"))

        repository.processPayMockPayment(50.0).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("PayMock failed with status: declined", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `processPayMockPayment emits failure when upstream result is a failure`() = runTest {
        val error = RuntimeException("paymock down")
        coEvery { payMockRemoteDataSource.createPayment(any()) } returns Result.failure(error)

        repository.processPayMockPayment(50.0).test {
            assertEquals(error, awaitItem().exceptionOrNull())
            awaitComplete()
        }
    }
}
