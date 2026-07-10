package com.tasneem.safwa.features.search.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.NoInternetException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchRepositoryImplTest {

    private val remoteDataSource = mockk<ProductRemoteDataSource>()
    private val repository = SearchRepositoryImpl(remoteDataSource)

    private fun dto(id: String) = ProductDto(
        id = id,
        handle = "handle-$id",
        title = "Title $id",
        description = null,
        vendor = "Safwa",
        productType = "Apparel",
        price = "10.0",
        currency = "EGP",
        imageUrls = emptyList(),
        imageAlts = emptyList()
    )

    @Test
    fun `searchProducts maps dtos to domain and forwards query and first`() = runTest {
        coEvery { remoteDataSource.searchProducts("shoes", 15) } returns listOf(dto("1"), dto("2"))

        val result = repository.searchProducts("shoes", 15)

        assertEquals(listOf("1", "2"), result.map { it.id })
        assertEquals("Title 1", result[0].title)
        coVerify(exactly = 1) { remoteDataSource.searchProducts("shoes", 15) }
    }

    @Test
    fun `searchProducts returns empty list when data source is empty`() = runTest {
        coEvery { remoteDataSource.searchProducts(any(), any()) } returns emptyList()

        val result = repository.searchProducts("nothing", 20)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchProducts propagates data source exceptions (no safeCall wrapping)`() = runTest {
        coEvery { remoteDataSource.searchProducts(any(), any()) } throws NoInternetException()

        val thrown = runCatching { repository.searchProducts("x", 20) }.exceptionOrNull()

        assertTrue(thrown is NoInternetException)
    }
}
