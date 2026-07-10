package com.tasneem.safwa.features.brand.domain.usecase

import app.cash.turbine.test
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.brand.domain.model.Brand
import com.tasneem.safwa.features.brand.domain.repository.BrandRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetBrandsUseCaseTest {

    private val repository = mockk<BrandRepository>()

    @Test
    fun `getBrands emits Loading then Success`() = runTest {
        val brands = listOf(Brand("adidas"), Brand("Nike"))
        coEvery { repository.getBrands() } returns brands

        GetBrandsUseCase(repository)().test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(brands), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getBrands emits Loading then Error with message and throwable`() = runTest {
        val cause = RuntimeException("brands failed")
        coEvery { repository.getBrands() } throws cause

        GetBrandsUseCase(repository)().test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertEquals("brands failed", error.message)
            assertEquals(cause, error.throwable)
            awaitComplete()
        }
    }
}
