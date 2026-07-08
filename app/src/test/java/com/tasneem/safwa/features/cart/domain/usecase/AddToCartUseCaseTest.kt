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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AddToCartUseCaseTest {

    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val cartRepository = mockk<CartRepository>()
    private val useCase = AddToCartUseCase(authRepository, cartRepository)

    private fun user(cartId: String) = User(id = "u1", cartId = cartId)

    @Test
    fun `existing cart id delegates to addToCart and returns its result`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user("gid://cart/1"))
        val expected = Resource.Success(Unit)
        coEvery { cartRepository.addToCart("gid://cart/1", "v1", 2) } returns expected

        val result = useCase("v1", 2)

        assertSame(expected, result)
        coVerify(exactly = 1) { cartRepository.addToCart("gid://cart/1", "v1", 2) }
    }

    @Test
    fun `empty cart id creates cart then persists new id`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(""))
        coEvery { cartRepository.createCart("v1", 2) } returns Resource.Success("gid://cart/new")

        val result = useCase("v1", 2)

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { authRepository.updateCartId("gid://cart/new") }
    }

    @Test
    fun `empty cart id with create failure returns error and does not persist id`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(""))
        coEvery { cartRepository.createCart(any(), any()) } returns Resource.Error("create failed")

        val result = useCase("v1", 2)

        assertEquals("create failed", (result as Resource.Error).message)
        coVerify(exactly = 0) { authRepository.updateCartId(any()) }
    }

    @Test
    fun `user not logged in returns error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Error("no user")

        val result = useCase("v1", 1)

        assertEquals("Failed to add to cart: User not found or not logged in.", (result as Resource.Error).message)
    }

    @Test
    fun `null user data returns error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(null)

        val result = useCase("v1", 1)

        assertTrue(result is Resource.Error)
    }
}
