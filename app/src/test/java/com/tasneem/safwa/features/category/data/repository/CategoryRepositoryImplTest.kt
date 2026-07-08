package com.tasneem.safwa.features.category.data.repository

import com.tasneem.network.datasource.product.ProductRemoteDataSource
import com.tasneem.network.dto.CategoryDto
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NoInternetException
import com.tasneem.safwa.core.exception.DomainException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CategoryRepositoryImplTest {

    private val remoteDataSource = mockk<ProductRemoteDataSource>()
    private val repository = CategoryRepositoryImpl(remoteDataSource)

    private fun categoryDto(id: String) = CategoryDto(
        id = id,
        title = "Title $id",
        handle = "handle-$id",
        description = "desc",
        imageUrl = "img-$id",
        imageAlt = "alt"
    )

    private fun productDto(id: String) = ProductDto(
        id = id,
        handle = "p-$id",
        title = "Product $id",
        description = null,
        vendor = "Safwa",
        productType = "Apparel",
        price = "10.0",
        currency = "EGP",
        imageUrls = emptyList(),
        imageAlts = emptyList()
    )

    @Test
    fun `getCategories maps dto to domain`() = runTest {
        coEvery { remoteDataSource.getCategories(50) } returns listOf(categoryDto("1"), categoryDto("2"))

        val result = repository.getCategories(50)

        assertEquals(listOf("1", "2"), result.map { it.id })
        assertEquals("Title 1", result[0].title)
        assertEquals("handle-1", result[0].handle)
        assertEquals("img-1", result[0].imageUrl)
    }

    @Test
    fun `getCategories wraps NoInternetException as DomainException NoInternet`() = runTest {
        coEvery { remoteDataSource.getCategories(any()) } throws NoInternetException()

        val thrown = runCatching { repository.getCategories(50) }.exceptionOrNull()

        assertTrue(thrown is DomainException.NoInternet)
    }

    @Test
    fun `getProductsByCategory normalizes handle and maps products`() = runTest {
        val handleSlot = slot<String>()
        coEvery { remoteDataSource.getCollectionProducts(capture(handleSlot), 20) } returns
            listOf(productDto("1"))

        val result = repository.getProductsByCategory("Men Shoes", 20)

        assertEquals("men-shoes", handleSlot.captured)
        assertEquals(listOf("1"), result.map { it.id })
    }

    @Test
    fun `getProductsByCategory leaves already-normalized handle unchanged`() = runTest {
        val handleSlot = slot<String>()
        coEvery { remoteDataSource.getCollectionProducts(capture(handleSlot), any()) } returns emptyList()

        repository.getProductsByCategory("shoes", 20)

        assertEquals("shoes", handleSlot.captured)
    }

    @Test
    fun `getProductsByCategory wraps GraphQlException as DomainException ServerError with reason`() = runTest {
        coEvery { remoteDataSource.getCollectionProducts(any(), any()) } throws GraphQlException("bad handle")

        val thrown = runCatching { repository.getProductsByCategory("x", 20) }.exceptionOrNull()

        assertTrue(thrown is DomainException.ServerError)
        assertEquals("bad handle", (thrown as DomainException.ServerError).reason)
    }

    @Test
    fun `getProductsByCategory wraps EmptyResponseException as DomainException NotFound`() = runTest {
        coEvery { remoteDataSource.getCollectionProducts(any(), any()) } throws EmptyResponseException()

        val thrown = runCatching { repository.getProductsByCategory("x", 20) }.exceptionOrNull()

        assertTrue(thrown is DomainException.NotFound)
    }
}
