package com.tasneem.safwa.features.checkout.domain.usecase

import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.model.Cart
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.domain.usecase.GetSavedCardsUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetCheckoutDataUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val cartRepository = mockk<CartRepository>()
    private val getAddresses = mockk<GetAddressesUseCase>()
    private val getSavedCards = mockk<GetSavedCardsUseCase>()

    private val useCase = GetCheckoutDataUseCase(authRepository, cartRepository, getAddresses, getSavedCards)

    private val cart = Cart(
        id = "gid://cart/1",
        checkoutUrl = "url",
        subtotalAmount = "100.0",
        totalAmount = "100.0",
        currency = "EGP",
        shippingAmount = null,
        lines = emptyList()
    )

    private fun user(
        cartId: String = "gid://cart/1",
        token: String? = "token123",
        email: String? = "buyer@example.com"
    ) = User(id = "u1", email = email, cartId = cartId, customerAccessToken = token)

    private val savedCard = SavedCard("c1", "VISA", "4242", "Tasneem", "12/30", isDefault = true)
    private val address = Address(id = "gid://address/1", street = "12 Nile St")

    @Test
    fun `happy path assembles checkout data`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user())
        coEvery { cartRepository.getCart("gid://cart/1") } returns Resource.Success(cart)
        coEvery { getAddresses("token123") } returns Result.success(listOf(address))
        every { getSavedCards() } returns flowOf(listOf(savedCard))

        val result = useCase()

        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertEquals(cart, data.cart)
        assertEquals(listOf(address), data.addresses)
        assertEquals(listOf(savedCard), data.savedCards)
        assertEquals("buyer@example.com", data.customerEmail)
    }

    @Test
    fun `user not success returns error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Error("no user")

        val result = useCase()

        assertTrue(result is Resource.Error)
        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }

    @Test
    fun `null user data returns error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(null)

        val result = useCase()

        assertEquals("User not logged in or not found", (result as Resource.Error).message)
    }

    @Test
    fun `empty cart id returns cart is empty error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(cartId = ""))

        val result = useCase()

        assertEquals("Cart is empty", (result as Resource.Error).message)
    }

    @Test
    fun `cart error is propagated with message and throwable`() = runTest {
        val cause = RuntimeException("network")
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user())
        coEvery { cartRepository.getCart(any()) } returns Resource.Error("cart failed", cause)

        val result = useCase()

        result as Resource.Error
        assertEquals("cart failed", result.message)
        assertEquals(cause, result.throwable)
    }

    @Test
    fun `cart loading returns still loading error`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user())
        coEvery { cartRepository.getCart(any()) } returns Resource.Loading

        val result = useCase()

        assertEquals("Cart is still loading", (result as Resource.Error).message)
    }

    @Test
    fun `null token skips address lookup and uses empty list`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user(token = null))
        coEvery { cartRepository.getCart(any()) } returns Resource.Success(cart)
        every { getSavedCards() } returns flowOf(emptyList())

        val result = useCase()

        assertEquals(emptyList<Address>(), (result as Resource.Success).data.addresses)
        coVerify(exactly = 0) { getAddresses(any()) }
    }

    @Test
    fun `address lookup failure falls back to empty list`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns Resource.Success(user())
        coEvery { cartRepository.getCart(any()) } returns Resource.Success(cart)
        coEvery { getAddresses("token123") } returns Result.failure(RuntimeException("boom"))
        every { getSavedCards() } returns flowOf(listOf(savedCard))

        val result = useCase()

        assertEquals(emptyList<Address>(), (result as Resource.Success).data.addresses)
    }
}
