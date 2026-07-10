package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.model.ApplyDiscountResult
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ApplyDiscountCodeUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val cartRepository = mockk<CartRepository>()
    private val useCase = ApplyDiscountCodeUseCase(authRepository, cartRepository)

    private fun user(cartId: String) = User(id = "u1", cartId = cartId)

    @Test
    fun `delegates to applyDiscountCode when cart id present`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user("gid://cart/1"))
        val expected = Resource.Success(
            ApplyDiscountResult(discountCodes = emptyList(), subtotalAmount = "100.0", totalAmount = "90.0", currency = "EGP")
        )
        coEvery { cartRepository.applyDiscountCode("gid://cart/1", listOf("SAVE10")) } returns expected

        val result = useCase(listOf("SAVE10"))

        assertSame(expected, result)
        coVerify(exactly = 1) { cartRepository.applyDiscountCode("gid://cart/1", listOf("SAVE10")) }
    }

    @Test
    fun `empty cart id returns error without calling repository`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(""))

        val result = useCase(listOf("SAVE10"))

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
        coVerify(exactly = 0) { cartRepository.applyDiscountCode(any(), any()) }
    }

    @Test
    fun `user not success returns error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Error("boom")

        val result = useCase(listOf("SAVE10"))

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }
}
