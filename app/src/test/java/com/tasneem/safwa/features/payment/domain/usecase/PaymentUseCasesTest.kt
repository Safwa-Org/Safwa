package com.tasneem.safwa.features.payment.domain.usecase

import com.tasneem.safwa.features.payment.domain.model.CardDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentDetails
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.domain.repository.PaymentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class PaymentUseCasesTest {

    private val repository = mockk<PaymentRepository>()

    @Test
    fun `GetSavedCardsUseCase returns the repository flow`() {
        val flow: Flow<List<SavedCard>> = flowOf(emptyList())
        every { repository.getSavedCards() } returns flow

        val result = GetSavedCardsUseCase(repository)()

        assertSame(flow, result)
        verify(exactly = 1) { repository.getSavedCards() }
    }

    @Test
    fun `SaveCardUseCase delegates to repository with the card details`() {
        val details = CardDetails("4242", "T", "A", "12", "2030", "123")
        val flow: Flow<Result<SavedCard>> = flowOf(Result.success(mockk()))
        every { repository.saveNewCard(details) } returns flow

        val result = SaveCardUseCase(repository)(details)

        assertSame(flow, result)
        verify(exactly = 1) { repository.saveNewCard(details) }
    }

    @Test
    fun `ProcessPaymentUseCase forwards arguments and returns the repository flow`() = runTest {
        val details = PaymentDetails(method = PaymentMethodType.VISA, cardId = "card_1")
        every { repository.processPayment("gid://order/1", 100.0, details) } returns flowOf(Result.success(Unit))

        val result = ProcessPaymentUseCase(repository)("gid://order/1", 100.0, details).first()

        assertEquals(Unit, result.getOrNull())
        verify(exactly = 1) { repository.processPayment("gid://order/1", 100.0, details) }
    }

    @Test
    fun `ProcessPayMockPaymentUseCase forwards amount and returns the repository flow`() = runTest {
        every { repository.processPayMockPayment(75.0) } returns flowOf(Result.success("pay_1"))

        val result = ProcessPayMockPaymentUseCase(repository)(75.0).first()

        assertEquals("pay_1", result.getOrNull())
        verify(exactly = 1) { repository.processPayMockPayment(75.0) }
    }
}
