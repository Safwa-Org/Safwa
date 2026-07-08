package com.tasneem.safwa.features.search.data.mapper

import com.tasneem.network.dto.ProductDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchMapperTest {

    private fun dto(
        description: String? = "A nice shirt",
        imageUrls: List<String?> = listOf("u1", "u2"),
        imageAlts: List<String?> = listOf("a1", "a2"),
        tags: List<String> = listOf("new", "sale")
    ) = ProductDto(
        id = "gid://product/1",
        handle = "shirt",
        title = "Shirt",
        description = description,
        vendor = "Safwa",
        productType = "Apparel",
        price = "19.99",
        currency = "EGP",
        imageUrls = imageUrls,
        imageAlts = imageAlts,
        tags = tags
    )

    @Test
    fun `maps all fields to domain`() {
        val product = dto().toDomainModel()

        assertEquals("gid://product/1", product.id)
        assertEquals("Shirt", product.title)
        assertEquals("shirt", product.handle)
        assertEquals("A nice shirt", product.description)
        assertEquals("Safwa", product.vendor)
        assertEquals("Apparel", product.productType)
        assertEquals("19.99", product.price)
        assertEquals("EGP", product.currency)
        assertEquals(listOf("u1", "u2"), product.imageUrl)
        assertEquals(listOf("a1", "a2"), product.imageAltText)
        assertEquals(listOf("new", "sale"), product.tags)
    }

    @Test
    fun `null description becomes empty string`() {
        val product = dto(description = null).toDomainModel()

        assertEquals("", product.description)
    }

    @Test
    fun `null entries in image lists are filtered out`() {
        val product = dto(
            imageUrls = listOf("u1", null, "u2"),
            imageAlts = listOf(null, "a1", null)
        ).toDomainModel()

        assertEquals(listOf("u1", "u2"), product.imageUrl)
        assertEquals(listOf("a1"), product.imageAltText)
    }

    @Test
    fun `empty tags are preserved`() {
        val product = dto(tags = emptyList()).toDomainModel()

        assertEquals(emptyList<String>(), product.tags)
    }
}
