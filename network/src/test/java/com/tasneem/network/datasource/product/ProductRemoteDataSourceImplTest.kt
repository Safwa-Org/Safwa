package com.tasneem.network.datasource.product

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestResponse
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.mapper.SearchProductMapper
import com.tasneem.safwa.network.GetCategoriesQuery
import com.tasneem.safwa.network.GetCollectionProductsQuery
import com.tasneem.safwa.network.SearchProductsQuery
import com.tasneem.safwa.network.type.CurrencyCode
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ApolloExperimental::class)
class ProductRemoteDataSourceImplTest {

    private val apolloClient = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()

    private val dataSource = ProductRemoteDataSourceImpl(
        apolloClient = apolloClient,
        mapper = mockk(relaxed = true),
        searchMapper = SearchProductMapper(),
        productDetailsDtoMapper = mockk(relaxed = true)
    )

    // ---------- searchProducts ----------

    private fun searchNode(id: String, altText: String? = "alt") = SearchProductsQuery.Node(
        id = id,
        title = "Title $id",
        handle = "handle-$id",
        vendor = "Safwa",
        productType = "Apparel",
        tags = listOf("new"),
        description = "desc-$id",
        priceRange = SearchProductsQuery.PriceRange(
            minVariantPrice = SearchProductsQuery.MinVariantPrice(
                amount = "19.99",
                currencyCode = CurrencyCode.USD
            )
        ),
        images = SearchProductsQuery.Images(
            edges = listOf(
                SearchProductsQuery.Edge1(
                    node = SearchProductsQuery.Node1(url = "http://img/$id", altText = altText)
                )
            )
        )
    )

    @Test
    fun `searchProducts maps graphql nodes to dtos`() = runTest {
        val data = SearchProductsQuery.Data(
            products = SearchProductsQuery.Products(
                edges = listOf(
                    SearchProductsQuery.Edge(node = searchNode("1")),
                    SearchProductsQuery.Edge(node = searchNode("2"))
                )
            )
        )
        apolloClient.enqueueTestResponse(SearchProductsQuery("shoes", 10), data)

        val result = dataSource.searchProducts("shoes", 10)

        assertEquals(listOf("1", "2"), result.map { it.id })
        val first = result[0]
        assertEquals("Title 1", first.title)
        assertEquals("handle-1", first.handle)
        assertEquals("Safwa", first.vendor)
        assertEquals("19.99", first.price)
        assertEquals("USD", first.currency)
        assertEquals(listOf("http://img/1"), first.imageUrls)
        assertEquals(listOf("alt"), first.imageAlts)
        assertEquals(listOf("new"), first.tags)
    }

    @Test
    fun `searchProducts returns empty list for empty edges`() = runTest {
        apolloClient.enqueueTestResponse(
            SearchProductsQuery("nothing", 10),
            SearchProductsQuery.Data(products = SearchProductsQuery.Products(edges = emptyList()))
        )

        val result = dataSource.searchProducts("nothing", 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchProducts throws GraphQlException when response has errors`() = runTest {
        apolloClient.enqueueTestResponse(
            SearchProductsQuery("x", 10),
            data = null,
            errors = listOf(Error.Builder("bad query").build())
        )

        val thrown = runCatching { dataSource.searchProducts("x", 10) }.exceptionOrNull()

        assertTrue(thrown is GraphQlException)
        assertEquals("bad query", (thrown as GraphQlException).errors)
    }

    @Test
    fun `searchProducts throws EmptyResponseException when data is null and no errors`() = runTest {
        apolloClient.enqueueTestResponse(SearchProductsQuery("x", 10), data = null)

        val thrown = runCatching { dataSource.searchProducts("x", 10) }.exceptionOrNull()

        assertTrue(thrown is EmptyResponseException)
    }

    // ---------- getCategories ----------

    @Test
    fun `getCategories maps collection nodes to category dtos`() = runTest {
        val data = GetCategoriesQuery.Data(
            collections = GetCategoriesQuery.Collections(
                edges = listOf(
                    GetCategoriesQuery.Edge(
                        node = GetCategoriesQuery.Node(
                            id = "gid://collection/1",
                            title = "Shoes",
                            handle = "shoes",
                            description = "desc",
                            image = GetCategoriesQuery.Image(url = "http://img/shoes", altText = "alt")
                        )
                    ),
                    GetCategoriesQuery.Edge(
                        node = GetCategoriesQuery.Node(
                            id = "gid://collection/2",
                            title = "Bags",
                            handle = "bags",
                            description = "desc2",
                            image = null
                        )
                    )
                )
            )
        )
        apolloClient.enqueueTestResponse(GetCategoriesQuery(50), data)

        val result = dataSource.getCategories(50)

        assertEquals(listOf("gid://collection/1", "gid://collection/2"), result.map { it.id })
        assertEquals("Shoes", result[0].title)
        assertEquals("http://img/shoes", result[0].imageUrl)
        // null image -> null imageUrl / imageAlt
        assertEquals(null, result[1].imageUrl)
        assertEquals(null, result[1].imageAlt)
    }

    // ---------- getCollectionProducts ----------

    private fun collectionNode(id: String) = GetCollectionProductsQuery.Node(
        id = id,
        title = "Title $id",
        handle = "handle-$id",
        vendor = "Safwa",
        priceRange = GetCollectionProductsQuery.PriceRange(
            minVariantPrice = GetCollectionProductsQuery.MinVariantPrice(
                amount = "30.0",
                currencyCode = CurrencyCode.USD
            )
        ),
        images = GetCollectionProductsQuery.Images(
            edges = listOf(
                GetCollectionProductsQuery.Edge1(
                    node = GetCollectionProductsQuery.Node1(url = "http://img/$id", altText = "alt-$id")
                )
            )
        )
    )

    private fun collectionQuery() = GetCollectionProductsQuery(handle = "shoes", first = 20)

    @Test
    fun `getCollectionProducts maps products and uses handle as product type`() = runTest {
        val data = GetCollectionProductsQuery.Data(
            collectionByHandle = GetCollectionProductsQuery.CollectionByHandle(
                id = "gid://collection/1",
                title = "Shoes",
                description = "desc",
                image = null,
                products = GetCollectionProductsQuery.Products(
                    edges = listOf(GetCollectionProductsQuery.Edge(node = collectionNode("1")))
                )
            )
        )
        apolloClient.enqueueTestResponse(collectionQuery(), data)

        val result = dataSource.getCollectionProducts("shoes", 20)

        assertEquals(1, result.size)
        val dto = result[0]
        assertEquals("1", dto.id)
        assertEquals("shoes", dto.productType) // handle passed as product type
        assertEquals("30.0", dto.price)
        assertEquals("USD", dto.currency)
        assertEquals(listOf("http://img/1"), dto.imageUrls)
        assertEquals("", dto.description) // always blank in this mapper
    }

    @Test
    fun `getCollectionProducts returns empty list when collection is null`() = runTest {
        apolloClient.enqueueTestResponse(
            collectionQuery(),
            GetCollectionProductsQuery.Data(collectionByHandle = null)
        )

        val result = dataSource.getCollectionProducts("shoes", 20)

        assertTrue(result.isEmpty())
    }
}
