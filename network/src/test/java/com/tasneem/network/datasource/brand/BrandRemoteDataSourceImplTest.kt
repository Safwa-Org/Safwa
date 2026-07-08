package com.tasneem.network.datasource.brand

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestResponse
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.mapper.VendorProductMapper
import com.tasneem.safwa.network.GetProductVendorsQuery
import com.tasneem.safwa.network.GetProductsForVendorQuery
import com.tasneem.safwa.network.type.CurrencyCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ApolloExperimental::class)
class BrandRemoteDataSourceImplTest {

    private val apolloClient = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()

    private val dataSource = BrandRemoteDataSourceImpl(
        apolloClient = apolloClient,
        vendorProductMapper = VendorProductMapper()
    )

    private fun vendorsPage(
        vendors: List<String>,
        hasNextPage: Boolean,
        endCursor: String?
    ) = GetProductVendorsQuery.Data(
        products = GetProductVendorsQuery.Products(
            pageInfo = GetProductVendorsQuery.PageInfo(hasNextPage = hasNextPage, endCursor = endCursor),
            nodes = vendors.map { GetProductVendorsQuery.Node(vendor = it) }
        )
    )

    @Test
    fun `getProductVendors returns single page of vendors`() = runTest {
        apolloClient.enqueueTestResponse(
            GetProductVendorsQuery(first = 250),
            vendorsPage(listOf("Nike", "Adidas"), hasNextPage = false, endCursor = "c1")
        )

        val result = dataSource.getProductVendors()

        assertEquals(listOf("Nike", "Adidas"), result)
    }

    @Test
    fun `getProductVendors concatenates all pages until hasNextPage is false`() = runTest {
        apolloClient.enqueueTestResponse(
            GetProductVendorsQuery(first = 250),
            vendorsPage(listOf("Nike"), hasNextPage = true, endCursor = "c1")
        )
        apolloClient.enqueueTestResponse(
            GetProductVendorsQuery(first = 250),
            vendorsPage(listOf("Adidas", "Zara"), hasNextPage = false, endCursor = "c2")
        )

        val result = dataSource.getProductVendors()

        assertEquals(listOf("Nike", "Adidas", "Zara"), result)
    }

    @Test
    fun `getProductVendors stops paginating when endCursor is null even if hasNextPage is true`() = runTest {
        apolloClient.enqueueTestResponse(
            GetProductVendorsQuery(first = 250),
            vendorsPage(listOf("Nike"), hasNextPage = true, endCursor = null)
        )

        val result = dataSource.getProductVendors()

        assertEquals(listOf("Nike"), result)
    }

    @Test
    fun `getProductsForVendor maps edges to product dtos`() = runTest {
        val node = GetProductsForVendorQuery.Node(
            id = "gid://product/1",
            title = "Air Max",
            handle = "air-max",
            vendor = "Nike",
            priceRange = GetProductsForVendorQuery.PriceRange(
                minVariantPrice = GetProductsForVendorQuery.MinVariantPrice(
                    amount = "120.0",
                    currencyCode = CurrencyCode.USD
                )
            ),
            images = GetProductsForVendorQuery.Images(
                edges = listOf(
                    GetProductsForVendorQuery.Edge1(
                        node = GetProductsForVendorQuery.Node1(url = "http://img/1", altText = "alt")
                    )
                )
            )
        )
        val data = GetProductsForVendorQuery.Data(
            products = GetProductsForVendorQuery.Products(
                pageInfo = GetProductsForVendorQuery.PageInfo(hasNextPage = false, endCursor = "c1"),
                edges = listOf(GetProductsForVendorQuery.Edge(node = node))
            )
        )
        apolloClient.enqueueTestResponse(
            GetProductsForVendorQuery(first = 250),
            data
        )

        val result = dataSource.getProductsForVendor("Nike")

        assertEquals(1, result.size)
        val dto = result[0]
        assertEquals("gid://product/1", dto.id)
        assertEquals("Nike", dto.vendor)
        assertEquals("120.0", dto.price)
        assertEquals("USD", dto.currency)
        assertEquals(listOf("http://img/1"), dto.imageUrls)
        assertEquals(null, dto.description) // VendorProductMapper sets description null
    }

    @Test
    fun `getProductsForVendor throws GraphQlException on graphql errors`() = runTest {
        apolloClient.enqueueTestResponse(
            GetProductsForVendorQuery(first = 250),
            data = null,
            errors = listOf(Error.Builder("boom").build())
        )

        val thrown = runCatching { dataSource.getProductsForVendor("Nike") }.exceptionOrNull()

        assertTrue(thrown is GraphQlException)
    }
}
