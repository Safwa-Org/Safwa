package com.tasneem.safwa.features.cart.data.mapper

import com.tasneem.network.dto.ApplyDiscountResultDto
import com.tasneem.network.dto.CartDto
import com.tasneem.network.dto.CartLineDto
import com.tasneem.network.dto.DiscountCodeDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CartDtoMapperTest {

    private fun lineDto(id: String, quantity: Int) = CartLineDto(
        id = id,
        variantId = "gid://variant/$id",
        productId = "gid://product/$id",
        vendor = "Safwa",
        productTitle = "Shirt $id",
        variantTitle = "M",
        productHandle = "shirt-$id",
        imageUrl = "img-$id",
        price = "10.0",
        totalLinePrice = "20.0",
        currency = "EGP",
        quantity = quantity
    )

    @Test
    fun `CartDto toDomain maps scalars nested lines and discount codes`() {
        val dto = CartDto(
            id = "gid://cart/1",
            checkoutUrl = "https://shop/checkout",
            subtotalAmount = "100.0",
            totalAmount = "119.0",
            currency = "EGP",
            shippingAmount = "5.0",
            shippingTitle = "Express",
            taxAmount = "14.0",
            discountCodes = listOf(
                DiscountCodeDto("SAVE10", applicable = true),
                DiscountCodeDto("EXPIRED", applicable = false)
            ),
            lines = listOf(lineDto("1", 2), lineDto("2", 1))
        )

        val cart = dto.toDomain()

        assertEquals("gid://cart/1", cart.id)
        assertEquals("https://shop/checkout", cart.checkoutUrl)
        assertEquals("100.0", cart.subtotalAmount)
        assertEquals("119.0", cart.totalAmount)
        assertEquals("EGP", cart.currency)
        assertEquals("5.0", cart.shippingAmount)
        assertEquals("Express", cart.shippingTitle)
        assertEquals("14.0", cart.taxAmount)
        // discount codes
        assertEquals(listOf("SAVE10", "EXPIRED"), cart.discountCodes.map { it.code })
        assertTrue(cart.discountCodes[0].applicable)
        assertEquals(false, cart.discountCodes[1].applicable)
        // lines
        assertEquals(listOf("1", "2"), cart.lines.map { it.id })
        assertEquals("gid://variant/1", cart.lines[0].variantId)
        assertEquals(2, cart.lines[0].quantity)
        assertEquals("Shirt 1", cart.lines[0].productTitle)
    }

    @Test
    fun `CartDto toDomain handles empty lines discounts and null optionals`() {
        val dto = CartDto(
            id = "gid://cart/2",
            checkoutUrl = "url",
            subtotalAmount = "0.0",
            totalAmount = "0.0",
            currency = "EGP",
            shippingAmount = null,
            shippingTitle = null,
            taxAmount = null,
            discountCodes = emptyList(),
            lines = emptyList()
        )

        val cart = dto.toDomain()

        assertNull(cart.shippingAmount)
        assertNull(cart.shippingTitle)
        assertNull(cart.taxAmount)
        assertTrue(cart.discountCodes.isEmpty())
        assertTrue(cart.lines.isEmpty())
    }

    @Test
    fun `ApplyDiscountResultDto toDomain maps codes amounts and currency`() {
        val dto = ApplyDiscountResultDto(
            discountCodes = listOf(
                DiscountCodeDto("SAVE10", applicable = true),
                DiscountCodeDto("BAD", applicable = false)
            ),
            subtotalAmount = "100.0",
            totalAmount = "90.0",
            currency = "EGP"
        )

        val result = dto.toDomain()

        assertEquals(listOf("SAVE10", "BAD"), result.discountCodes.map { it.code })
        assertTrue(result.discountCodes[0].applicable)
        assertEquals(false, result.discountCodes[1].applicable)
        assertEquals("100.0", result.subtotalAmount)
        assertEquals("90.0", result.totalAmount)
        assertEquals("EGP", result.currency)
    }
}
