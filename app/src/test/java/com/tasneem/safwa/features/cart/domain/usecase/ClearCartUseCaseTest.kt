package com.tasneem.safwa.features.cart.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ClearCartUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val cartRepository = mockk<CartRepository>(relaxed = true)
    private val useCase = ClearCartUseCase(authRepository, cartRepository)

    @Test
    fun `clears local cart when cart id reset succeeds`() = runTest {
        coEvery { authRepository.updateCartId("") } returns Resource.Success(Unit)

        val result = useCase()

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { cartRepository.clearCartLocal() }
    }

    @Test
    fun `does not clear local cart when cart id reset fails`() = runTest {
        val error = Resource.Error("update failed")
        coEvery { authRepository.updateCartId("") } returns error

        val result = useCase()

        assertSame(error, result)
        coVerify(exactly = 0) { cartRepository.clearCartLocal() }
    }
}
