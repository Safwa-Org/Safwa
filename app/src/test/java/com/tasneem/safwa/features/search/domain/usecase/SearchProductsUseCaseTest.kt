package com.tasneem.safwa.features.search.domain.usecase

import app.cash.turbine.test
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.search.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchProductsUseCaseTest {

    private val repository = mockk<SearchRepository>()
    private val useCase = SearchProductsUseCase(repository)

    @Test
    fun `emits Loading then Success and forwards query and first`() = runTest {
        val products = listOf(Product(id = "1"), Product(id = "2"))
        coEvery { repository.searchProducts("shoes", 15) } returns products

        useCase("shoes", 15).test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(products), awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.searchProducts("shoes", 15) }
    }

    @Test
    fun `emits Loading then Error with localized message on failure`() = runTest {
        coEvery { repository.searchProducts(any(), any()) } throws RuntimeException("network down")

        useCase("shoes").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals("network down", (error as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `uses fallback message when exception has no message`() = runTest {
        coEvery { repository.searchProducts(any(), any()) } throws RuntimeException()

        useCase("shoes").test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals("Failed to search products", (awaitItem() as Resource.Error).message)
            awaitComplete()
        }
    }
}
