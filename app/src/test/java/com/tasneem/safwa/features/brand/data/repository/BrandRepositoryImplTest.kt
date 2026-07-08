package com.tasneem.safwa.features.brand.data.repository

import com.tasneem.network.datasource.brand.BrandRemoteDataSource
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.NoInternetException
import com.tasneem.safwa.core.exception.DomainException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BrandRepositoryImplTest {

    private val remoteDataSource = mockk<BrandRemoteDataSource>()
    private val repository = BrandRepositoryImpl(remoteDataSource)

    private fun productDto(id: String) = ProductDto(
        id = id,
        handle = "p-$id",
        title = "Product $id",
        description = null,
        vendor = "Nike",
        productType = "Apparel",
        price = "10.0",
        currency = "EGP",
        imageUrls = emptyList(),
        imageAlts = emptyList()
    )

    @Test
    fun `getBrands filters blanks dedupes and sorts case-insensitively`() = runTest {
        coEvery { remoteDataSource.getProductVendors() } returns
            listOf("Nike", "", "adidas", "Nike", "Zara", "  ")

        val result = repository.getBrands()

        // blanks removed, "Nike" deduped, sorted by lowercase -> adidas, Nike, Zara
        assertEquals(listOf("adidas", "Nike", "Zara"), result.map { it.name })
    }

    @Test
    fun `getBrands caches result and does not re-hit remote on second call`() = runTest {
        coEvery { remoteDataSource.getProductVendors() } returns listOf("Nike")

        val first = repository.getBrands()
        val second = repository.getBrands()

        assertEquals(first, second)
        coVerify(exactly = 1) { remoteDataSource.getProductVendors() }
    }

    @Test
    fun `getBrands returns empty list when no vendors`() = runTest {
        coEvery { remoteDataSource.getProductVendors() } returns emptyList()

        val result = repository.getBrands()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getBrands wraps NetworkException as DomainException NetworkError`() = runTest {
        coEvery { remoteDataSource.getProductVendors() } throws NetworkException()

        val thrown = runCatching { repository.getBrands() }.exceptionOrNull()

        assertTrue(thrown is DomainException.NetworkError)
    }

    @Test
    fun `getBrands failure is not cached and remote is hit again on retry`() = runTest {
        coEvery { remoteDataSource.getProductVendors() } throws NetworkException() andThen listOf("Nike")

        runCatching { repository.getBrands() }
        val second = repository.getBrands()

        assertEquals(listOf("Nike"), second.map { it.name })
        coVerify(exactly = 2) { remoteDataSource.getProductVendors() }
    }

    @Test
    fun `getProductsByBrand maps products and forwards brand name`() = runTest {
        coEvery { remoteDataSource.getProductsForVendor("Nike") } returns listOf(productDto("1"), productDto("2"))

        val result = repository.getProductsByBrand("Nike")

        assertEquals(listOf("1", "2"), result.map { it.id })
        coVerify(exactly = 1) { remoteDataSource.getProductsForVendor("Nike") }
    }

    @Test
    fun `getProductsByBrand wraps NoInternetException as DomainException NoInternet`() = runTest {
        coEvery { remoteDataSource.getProductsForVendor(any()) } throws NoInternetException()

        val thrown = runCatching { repository.getProductsByBrand("Nike") }.exceptionOrNull()

        assertTrue(thrown is DomainException.NoInternet)
    }
}
