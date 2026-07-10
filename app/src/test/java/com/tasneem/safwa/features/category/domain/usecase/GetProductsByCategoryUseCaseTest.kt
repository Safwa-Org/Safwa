package com.tasneem.safwa.features.category.domain.usecase

import app.cash.turbine.test
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.category.domain.repository.CategoryRepository
import com.tasneem.safwa.features.core.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetProductsByCategoryUseCaseTest {

    private val repository = mockk<CategoryRepository>()
    private val useCase = GetProductsByCategoryUseCase(repository)

    @Test
    fun `emits Loading then Success and forwards category and first`() = runTest {
        val products = listOf(Product(id = "1"))
        coEvery { repository.getProductsByCategory("shoes", 10) } returns products

        useCase("shoes", 10).test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(products), awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.getProductsByCategory("shoes", 10) }
    }

    @Test
    fun `emits Loading then Error carrying message and throwable on failure`() = runTest {
        val cause = RuntimeException("boom")
        coEvery { repository.getProductsByCategory(any(), any()) } throws cause

        useCase("shoes").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            error as Resource.Error
            assertEquals("boom", error.message)
            assertEquals(cause, error.throwable)
            awaitComplete()
        }
    }
}
