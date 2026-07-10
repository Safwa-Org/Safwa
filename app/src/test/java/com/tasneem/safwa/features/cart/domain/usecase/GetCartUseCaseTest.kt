package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class GetCartUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val cartRepository = mockk<CartRepository>()
    private val useCase = GetCartUseCase(authRepository, cartRepository)

    private fun user(cartId: String) = User(id = "u1", cartId = cartId)

    private val cart = Cart(
        id = "gid://cart/1",
        checkoutUrl = "url",
        subtotalAmount = "100.0",
        totalAmount = "100.0",
        currency = "EGP",
        shippingAmount = null,
        lines = emptyList()
    )

    @Test
    fun `cart id present delegates to getCart`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user("gid://cart/1"))
        val expected = Resource.Success(cart)
        coEvery { cartRepository.getCart("gid://cart/1") } returns expected

        val result = useCase()

        assertSame(expected, result)
    }

    @Test
    fun `empty cart id returns cart is empty`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(""))

        val result = useCase()

        assertEquals("Cart is empty", (result as Resource.Error).message)
    }

    @Test
    fun `user not success returns not logged in error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Error("boom")

        val result = useCase()

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }

    @Test
    fun `null user data returns not logged in error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(null)

        val result = useCase()

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }
}
