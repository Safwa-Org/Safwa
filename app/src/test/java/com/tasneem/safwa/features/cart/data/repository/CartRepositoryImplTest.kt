package com.tasneem.safwa.features.cart.data.repository

import com.tasneem.network.dto.ApplyDiscountResultDto
import com.tasneem.network.dto.CartDto
import com.tasneem.network.dto.CartLineDto
import com.tasneem.network.dto.DiscountCodeDto
import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.safwa.core.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CartRepositoryImplTest {

    private val remoteDataSource = mockk<CartRemoteDataSource>()
    private val repository = CartRepositoryImpl(remoteDataSource)

    private fun cartDto(vararg quantities: Int) = CartDto(
        id = "gid://cart/1",
        checkoutUrl = "url",
        subtotalAmount = "100.0",
        totalAmount = "100.0",
        currency = "EGP",
        shippingAmount = null,
        lines = quantities.mapIndexed { i, q ->
            CartLineDto(
                id = "line$i",
                variantId = "v$i",
                productId = "p$i",
                vendor = "Safwa",
                productTitle = "P$i",
                variantTitle = "M",
                productHandle = "h$i",
                imageUrl = "img",
                price = "10.0",
                totalLinePrice = "${10 * q}.0",
                currency = "EGP",
                quantity = q
            )
        }
    )

    @Test
    fun `cartItemCount starts at zero`() {
        assertEquals(0, repository.cartItemCount.value)
    }

    // ---- createCart ----

    @Test
    fun `createCart success returns id and sets count to quantity`() = runTest {
        coEvery { remoteDataSource.createCart("v1", 3) } returns "gid://cart/1"

        val result = repository.createCart("v1", 3)

        assertEquals("gid://cart/1", (result as Resource.Success).data)
        assertEquals(3, repository.cartItemCount.value)
    }

    @Test
    fun `createCart failure returns error and leaves count unchanged`() = runTest {
        coEvery { remoteDataSource.createCart(any(), any()) } throws RuntimeException("boom")

        val result = repository.createCart("v1", 3)

        assertEquals("boom", (result as Resource.Error).message)
        assertEquals(0, repository.cartItemCount.value)
    }

    @Test
    fun `createCart failure without message uses fallback`() = runTest {
        coEvery { remoteDataSource.createCart(any(), any()) } throws RuntimeException()

        val result = repository.createCart("v1", 1)

        assertEquals("Failed to create cart", (result as Resource.Error).message)
    }

    // ---- addToCart ----

    @Test
    fun `addToCart success increments count by quantity`() = runTest {
        coEvery { remoteDataSource.createCart("v1", 2) } returns "gid://cart/1"
        repository.createCart("v1", 2) // count = 2
        coEvery { remoteDataSource.addToCart("gid://cart/1", "v2", 3) } returns Unit

        val result = repository.addToCart("gid://cart/1", "v2", 3)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(5, repository.cartItemCount.value)
    }

    @Test
    fun `addToCart failure returns error and leaves count unchanged`() = runTest {
        coEvery { remoteDataSource.addToCart(any(), any(), any()) } throws RuntimeException("nope")

        val result = repository.addToCart("gid://cart/1", "v2", 3)

        assertEquals("nope", (result as Resource.Error).message)
        assertEquals(0, repository.cartItemCount.value)
    }

    // ---- getCart ----

    @Test
    fun `getCart success maps cart and sets count to sum of line quantities`() = runTest {
        coEvery { remoteDataSource.getCart("gid://cart/1") } returns cartDto(2, 3, 1)

        val result = repository.getCart("gid://cart/1")

        val cart = (result as Resource.Success).data
        assertEquals("gid://cart/1", cart.id)
        assertEquals(3, cart.lines.size)
        assertEquals(6, repository.cartItemCount.value)
    }

    @Test
    fun `getCart failure returns error with fallback`() = runTest {
        coEvery { remoteDataSource.getCart(any()) } throws RuntimeException()

        val result = repository.getCart("gid://cart/1")

        assertEquals("Failed to fetch cart", (result as Resource.Error).message)
    }

    // ---- removeFromCart ----

    @Test
    fun `removeFromCart success returns total and decrements count`() = runTest {
        coEvery { remoteDataSource.getCart("gid://cart/1") } returns cartDto(2, 3) // count = 5
        repository.getCart("gid://cart/1")
        coEvery { remoteDataSource.removeFromCart("gid://cart/1", listOf("line0")) } returns "30.0"

        val result = repository.removeFromCart("gid://cart/1", listOf("line0"), removedQuantity = 2)

        assertEquals("30.0", (result as Resource.Success).data)
        assertEquals(3, repository.cartItemCount.value)
    }

    @Test
    fun `removeFromCart failure returns error`() = runTest {
        coEvery { remoteDataSource.removeFromCart(any(), any()) } throws RuntimeException("x")

        val result = repository.removeFromCart("gid://cart/1", listOf("line0"), 2)

        assertEquals("x", (result as Resource.Error).message)
    }

    // ---- updateCartLine ----

    @Test
    fun `updateCartLine success applies positive difference to count`() = runTest {
        coEvery { remoteDataSource.getCart("gid://cart/1") } returns cartDto(2) // count = 2
        repository.getCart("gid://cart/1")
        coEvery { remoteDataSource.updateCartLine("gid://cart/1", "line0", 4) } returns "40.0"

        val result = repository.updateCartLine("gid://cart/1", "line0", quantity = 4, difference = 2)

        assertEquals("40.0", (result as Resource.Success).data)
        assertEquals(4, repository.cartItemCount.value)
    }

    @Test
    fun `updateCartLine success applies negative difference to count`() = runTest {
        coEvery { remoteDataSource.getCart("gid://cart/1") } returns cartDto(5) // count = 5
        repository.getCart("gid://cart/1")
        coEvery { remoteDataSource.updateCartLine("gid://cart/1", "line0", 2) } returns "20.0"

        repository.updateCartLine("gid://cart/1", "line0", quantity = 2, difference = -3)

        assertEquals(2, repository.cartItemCount.value)
    }

    @Test
    fun `updateCartLine failure returns error`() = runTest {
        coEvery { remoteDataSource.updateCartLine(any(), any(), any()) } throws RuntimeException("x")

        val result = repository.updateCartLine("gid://cart/1", "line0", 2, -1)

        assertEquals("x", (result as Resource.Error).message)
    }

    // ---- applyDiscountCode ----

    @Test
    fun `applyDiscountCode success maps result`() = runTest {
        coEvery { remoteDataSource.applyDiscountCode("gid://cart/1", listOf("SAVE10")) } returns
            ApplyDiscountResultDto(
                discountCodes = listOf(DiscountCodeDto("SAVE10", applicable = true)),
                subtotalAmount = "100.0",
                totalAmount = "90.0",
                currency = "EGP"
            )

        val result = repository.applyDiscountCode("gid://cart/1", listOf("SAVE10"))

        val data = (result as Resource.Success).data
        assertEquals("SAVE10", data.discountCodes.single().code)
        assertEquals("90.0", data.totalAmount)
    }

    @Test
    fun `applyDiscountCode failure returns error with fallback`() = runTest {
        coEvery { remoteDataSource.applyDiscountCode(any(), any()) } throws RuntimeException()

        val result = repository.applyDiscountCode("gid://cart/1", listOf("SAVE10"))

        assertEquals("Failed to apply discount code", (result as Resource.Error).message)
    }

    // ---- clearCartLocal ----

    @Test
    fun `clearCartLocal resets count to zero`() = runTest {
        coEvery { remoteDataSource.createCart("v1", 4) } returns "gid://cart/1"
        repository.createCart("v1", 4) // count = 4

        repository.clearCartLocal()

        assertEquals(0, repository.cartItemCount.value)
    }
}
