package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class UpdateCartLineUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val cartRepository = mockk<CartRepository>()
    private val useCase = UpdateCartLineUseCase(authRepository, cartRepository)

    private fun user(cartId: String) = User(id = "u1", cartId = cartId)

    @Test
    fun `delegates to updateCartLine forwarding line quantity and difference`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user("gid://cart/1"))
        val expected = Resource.Success("40.0")
        coEvery { cartRepository.updateCartLine("gid://cart/1", "line1", 4, 2) } returns expected

        val result = useCase("line1", quantity = 4, difference = 2)

        assertSame(expected, result)
        coVerify(exactly = 1) { cartRepository.updateCartLine("gid://cart/1", "line1", 4, 2) }
    }

    @Test
    fun `empty cart id returns cart is empty`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(""))

        val result = useCase("line1", 4, 2)

        assertEquals("Cart is empty", (result as Resource.Error).message)
    }

    @Test
    fun `user not logged in returns not logged in error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Error("boom")

        val result = useCase("line1", 4, 2)

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }
}
