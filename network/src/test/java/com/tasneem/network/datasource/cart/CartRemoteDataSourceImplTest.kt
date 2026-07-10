package com.tasneem.network.datasource.cart

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestResponse
import com.tasneem.safwa.network.AddToCartMutation
import com.tasneem.safwa.network.ApplyDiscountCodeMutation
import com.tasneem.safwa.network.CreateCartMutation
import com.tasneem.safwa.network.GetCartQuery
import com.tasneem.safwa.network.RemoveFromCartMutation
import com.tasneem.safwa.network.UpdateCartLineMutation
import com.tasneem.safwa.network.type.CartInput
import com.tasneem.safwa.network.type.CurrencyCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ApolloExperimental::class)
class CartRemoteDataSourceImplTest {

    private val apolloClient = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()

    private val dataSource = CartRemoteDataSourceImpl(apolloClient)

    // ---------- createCart ----------

    private fun createCartData(cart: CreateCartMutation.Cart?, errors: List<CreateCartMutation.UserError> = emptyList()) =
        CreateCartMutation.Data(
            cartCreate = CreateCartMutation.CartCreate(cart = cart, userErrors = errors)
        )

    private fun createdCart(id: String) = CreateCartMutation.Cart(
        id = id,
        checkoutUrl = "https://shop/checkout",
        createdAt = "2024-01-01",
        updatedAt = "2024-01-01",
        lines = CreateCartMutation.Lines(edges = emptyList()),
        cost = CreateCartMutation.Cost(
            subtotalAmount = CreateCartMutation.SubtotalAmount("100.0", CurrencyCode.USD),
            totalAmount = CreateCartMutation.TotalAmount("100.0", CurrencyCode.USD)
        )
    )

    @Test
    fun `createCart returns the new cart id`() = runTest {
        apolloClient.enqueueTestResponse(
            CreateCartMutation(input = CartInput()),
            createCartData(createdCart("gid://cart/1"))
        )

        val result = dataSource.createCart("gid://variant/1", 2)

        assertEquals("gid://cart/1", result)
    }

    @Test
    fun `createCart throws first user error message`() = runTest {
        apolloClient.enqueueTestResponse(
            CreateCartMutation(input = CartInput()),
            createCartData(
                cart = null,
                errors = listOf(CreateCartMutation.UserError(`field` = null, message = "Invalid variant"))
            )
        )

        val thrown = runCatching { dataSource.createCart("bad", 1) }.exceptionOrNull()

        assertEquals("Invalid variant", thrown?.message)
    }

    @Test
    fun `createCart throws when cart id is null`() = runTest {
        apolloClient.enqueueTestResponse(
            CreateCartMutation(input = CartInput()),
            createCartData(cart = null)
        )

        val thrown = runCatching { dataSource.createCart("v1", 1) }.exceptionOrNull()

        assertEquals("Failed to create cart, ID is null", thrown?.message)
    }

    // ---------- addToCart ----------

    @Test
    fun `addToCart succeeds when there are no user errors`() = runTest {
        apolloClient.enqueueTestResponse(
            AddToCartMutation("gid://cart/1", emptyList()),
            AddToCartMutation.Data(
                cartLinesAdd = AddToCartMutation.CartLinesAdd(cart = null, userErrors = emptyList())
            )
        )

        // Should not throw
        dataSource.addToCart("gid://cart/1", "v1", 1)
    }

    @Test
    fun `addToCart throws first user error message`() = runTest {
        apolloClient.enqueueTestResponse(
            AddToCartMutation("gid://cart/1", emptyList()),
            AddToCartMutation.Data(
                cartLinesAdd = AddToCartMutation.CartLinesAdd(
                    cart = null,
                    userErrors = listOf(AddToCartMutation.UserError(`field` = null, message = "Out of stock"))
                )
            )
        )

        val thrown = runCatching { dataSource.addToCart("gid://cart/1", "v1", 1) }.exceptionOrNull()

        assertEquals("Out of stock", thrown?.message)
    }

    // ---------- getCart ----------

    private fun fullCart() = GetCartQuery.Cart(
        id = "gid://cart/1",
        checkoutUrl = "https://shop/checkout",
        discountCodes = listOf(GetCartQuery.DiscountCode(code = "SAVE10", applicable = true)),
        lines = GetCartQuery.Lines(
            edges = listOf(
                GetCartQuery.Edge(
                    node = GetCartQuery.Node(
                        id = "line1",
                        quantity = 2,
                        merchandise = GetCartQuery.Merchandise(
                            __typename = "ProductVariant",
                            onProductVariant = GetCartQuery.OnProductVariant(
                                id = "gid://variant/1",
                                title = "M",
                                product = GetCartQuery.Product(
                                    title = "Shirt",
                                    featuredImage = GetCartQuery.FeaturedImage(url = "http://img/1")
                                ),
                                priceV2 = GetCartQuery.PriceV2("10.0", CurrencyCode.USD)
                            )
                        ),
                        cost = GetCartQuery.Cost(totalAmount = GetCartQuery.TotalAmount("20.0", CurrencyCode.USD))
                    )
                )
            )
        ),
        cost = GetCartQuery.Cost1(
            subtotalAmount = GetCartQuery.SubtotalAmount("100.0", CurrencyCode.USD),
            totalTaxAmount = GetCartQuery.TotalTaxAmount("14.0", CurrencyCode.USD),
            totalAmount = GetCartQuery.TotalAmount1("119.0", CurrencyCode.USD)
        ),
        deliveryGroups = GetCartQuery.DeliveryGroups(
            edges = listOf(
                GetCartQuery.Edge1(
                    node = GetCartQuery.Node1(
                        deliveryOptions = listOf(
                            GetCartQuery.DeliveryOption(
                                title = "Standard",
                                handle = "standard",
                                estimatedCost = GetCartQuery.EstimatedCost("5.0", CurrencyCode.USD)
                            )
                        )
                    )
                )
            )
        )
    )

    @Test
    fun `getCart maps the full cart into a dto`() = runTest {
        apolloClient.enqueueTestResponse(GetCartQuery("gid://cart/1"), GetCartQuery.Data(cart = fullCart()))

        val dto = dataSource.getCart("gid://cart/1")

        assertEquals("gid://cart/1", dto.id)
        assertEquals("100.0", dto.subtotalAmount)
        assertEquals("119.0", dto.totalAmount)
        assertEquals("USD", dto.currency)
        assertEquals("5.0", dto.shippingAmount)
        assertEquals("Standard", dto.shippingTitle)
        assertEquals("14.0", dto.taxAmount)
        assertEquals(listOf("SAVE10"), dto.discountCodes.map { it.code })
        assertEquals(1, dto.lines.size)
        val line = dto.lines[0]
        assertEquals("line1", line.id)
        assertEquals("gid://variant/1", line.variantId)
        assertEquals("Shirt", line.productTitle)
        assertEquals("M", line.variantTitle)
        assertEquals("http://img/1", line.imageUrl)
        assertEquals("10.0", line.price)
        assertEquals("20.0", line.totalLinePrice)
        assertEquals(2, line.quantity)
    }

    @Test
    fun `getCart throws when cart is null`() = runTest {
        apolloClient.enqueueTestResponse(GetCartQuery("missing"), GetCartQuery.Data(cart = null))

        val thrown = runCatching { dataSource.getCart("missing") }.exceptionOrNull()

        assertEquals("Cart not found", thrown?.message)
    }

    // ---------- removeFromCart ----------

    private fun removeData(cart: RemoveFromCartMutation.Cart?, errors: List<RemoveFromCartMutation.UserError> = emptyList()) =
        RemoveFromCartMutation.Data(
            cartLinesRemove = RemoveFromCartMutation.CartLinesRemove(cart = cart, userErrors = errors)
        )

    @Test
    fun `removeFromCart returns updated total amount`() = runTest {
        val cart = RemoveFromCartMutation.Cart(
            id = "gid://cart/1",
            lines = RemoveFromCartMutation.Lines(edges = emptyList()),
            cost = RemoveFromCartMutation.Cost(RemoveFromCartMutation.TotalAmount("30.0", CurrencyCode.USD))
        )
        apolloClient.enqueueTestResponse(RemoveFromCartMutation("gid://cart/1", listOf("line1")), removeData(cart))

        val result = dataSource.removeFromCart("gid://cart/1", listOf("line1"))

        assertEquals("30.0", result)
    }

    @Test
    fun `removeFromCart returns zero fallback when cart is null`() = runTest {
        apolloClient.enqueueTestResponse(RemoveFromCartMutation("gid://cart/1", listOf("line1")), removeData(cart = null))

        val result = dataSource.removeFromCart("gid://cart/1", listOf("line1"))

        assertEquals("0.0", result)
    }

    @Test
    fun `removeFromCart throws first user error`() = runTest {
        apolloClient.enqueueTestResponse(
            RemoveFromCartMutation("gid://cart/1", listOf("line1")),
            removeData(cart = null, errors = listOf(RemoveFromCartMutation.UserError(`field` = null, message = "Line not found")))
        )

        val thrown = runCatching { dataSource.removeFromCart("gid://cart/1", listOf("line1")) }.exceptionOrNull()

        assertEquals("Line not found", thrown?.message)
    }

    // ---------- updateCartLine ----------

    @Test
    fun `updateCartLine returns updated total amount`() = runTest {
        val cart = UpdateCartLineMutation.Cart(
            id = "gid://cart/1",
            lines = UpdateCartLineMutation.Lines(edges = emptyList()),
            cost = UpdateCartLineMutation.Cost(UpdateCartLineMutation.TotalAmount("45.0", CurrencyCode.USD))
        )
        apolloClient.enqueueTestResponse(
            UpdateCartLineMutation("gid://cart/1", emptyList()),
            UpdateCartLineMutation.Data(
                cartLinesUpdate = UpdateCartLineMutation.CartLinesUpdate(cart = cart, userErrors = emptyList())
            )
        )

        val result = dataSource.updateCartLine("gid://cart/1", "line1", 3)

        assertEquals("45.0", result)
    }

    @Test
    fun `updateCartLine throws first user error`() = runTest {
        apolloClient.enqueueTestResponse(
            UpdateCartLineMutation("gid://cart/1", emptyList()),
            UpdateCartLineMutation.Data(
                cartLinesUpdate = UpdateCartLineMutation.CartLinesUpdate(
                    cart = null,
                    userErrors = listOf(UpdateCartLineMutation.UserError(`field` = null, message = "Invalid quantity"))
                )
            )
        )

        val thrown = runCatching { dataSource.updateCartLine("gid://cart/1", "line1", 0) }.exceptionOrNull()

        assertEquals("Invalid quantity", thrown?.message)
    }

    // ---------- applyDiscountCode ----------

    private fun discountCart() = ApplyDiscountCodeMutation.Cart(
        id = "gid://cart/1",
        discountCodes = listOf(
            ApplyDiscountCodeMutation.DiscountCode(code = "SAVE10", applicable = true),
            ApplyDiscountCodeMutation.DiscountCode(code = "BAD", applicable = false)
        ),
        cost = ApplyDiscountCodeMutation.Cost(
            subtotalAmount = ApplyDiscountCodeMutation.SubtotalAmount("100.0", CurrencyCode.USD),
            totalAmount = ApplyDiscountCodeMutation.TotalAmount("90.0", CurrencyCode.USD)
        )
    )

    @Test
    fun `applyDiscountCode maps cart into result dto`() = runTest {
        apolloClient.enqueueTestResponse(
            ApplyDiscountCodeMutation("gid://cart/1", listOf("SAVE10")),
            ApplyDiscountCodeMutation.Data(
                cartDiscountCodesUpdate = ApplyDiscountCodeMutation.CartDiscountCodesUpdate(
                    cart = discountCart(),
                    userErrors = emptyList(),
                    warnings = emptyList()
                )
            )
        )

        val result = dataSource.applyDiscountCode("gid://cart/1", listOf("SAVE10"))

        assertEquals(listOf("SAVE10", "BAD"), result.discountCodes.map { it.code })
        assertTrue(result.discountCodes[0].applicable)
        assertEquals(false, result.discountCodes[1].applicable)
        assertEquals("100.0", result.subtotalAmount)
        assertEquals("90.0", result.totalAmount)
        assertEquals("USD", result.currency)
    }

    @Test
    fun `applyDiscountCode throws when cart is null`() = runTest {
        apolloClient.enqueueTestResponse(
            ApplyDiscountCodeMutation("gid://cart/1", listOf("SAVE10")),
            ApplyDiscountCodeMutation.Data(
                cartDiscountCodesUpdate = ApplyDiscountCodeMutation.CartDiscountCodesUpdate(
                    cart = null,
                    userErrors = emptyList(),
                    warnings = emptyList()
                )
            )
        )

        val thrown = runCatching { dataSource.applyDiscountCode("gid://cart/1", listOf("SAVE10")) }.exceptionOrNull()

        assertEquals("Failed to apply discount code", thrown?.message)
    }

    @Test
    fun `applyDiscountCode throws first user error`() = runTest {
        apolloClient.enqueueTestResponse(
            ApplyDiscountCodeMutation("gid://cart/1", listOf("SAVE10")),
            ApplyDiscountCodeMutation.Data(
                cartDiscountCodesUpdate = ApplyDiscountCodeMutation.CartDiscountCodesUpdate(
                    cart = null,
                    userErrors = listOf(ApplyDiscountCodeMutation.UserError(`field` = null, message = "Invalid code")),
                    warnings = emptyList()
                )
            )
        )

        val thrown = runCatching { dataSource.applyDiscountCode("gid://cart/1", listOf("SAVE10")) }.exceptionOrNull()

        assertEquals("Invalid code", thrown?.message)
    }
}
