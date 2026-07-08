package com.tasneem.safwa.features.brand.domain.usecase

import app.cash.turbine.test
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import com.tasneem.safwa.features.core.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetBrandProductsUseCaseTest {

    private val repository = mockk<BrandRepository>()

    @Test
    fun `getBrandProducts emits Loading then Success and forwards brand name`() = runTest {
        val products = listOf(Product(id = "1"))
        coEvery { repository.getProductsByBrand("Nike") } returns products

        GetBrandProductsUseCase(repository)("Nike").test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(products), awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.getProductsByBrand("Nike") }
    }

    @Test
    fun `getBrandProducts emits Loading then Error on failure`() = runTest {
        coEvery { repository.getProductsByBrand(any()) } throws RuntimeException("boom")

        GetBrandProductsUseCase(repository)("Nike").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals("boom", (error as Resource.Error).message)
            awaitComplete()
        }
    }
}
