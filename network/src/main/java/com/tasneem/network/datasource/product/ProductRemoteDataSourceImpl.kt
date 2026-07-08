package com.tasneem.network.datasource.product

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.dto.PaginatedResultDto
import com.tasneem.network.dto.CategoryDto
import com.tasneem.network.exception.safeApiCall
import com.tasneem.network.mapper.ProductDetailsDtoMapper
import com.tasneem.network.mapper.ProductMapper
import com.tasneem.network.mapper.mapList
import com.tasneem.network.mapper.SearchProductMapper
import com.tasneem.safwa.network.GetCategoriesQuery
import com.tasneem.safwa.network.GetCollectionProductsQuery
import com.tasneem.safwa.network.GetProductByHandleQuery
import com.tasneem.safwa.network.ProductsQuery
import com.tasneem.safwa.network.SearchProductsQuery
import com.tasneem.safwa.network.type.ProductCollectionSortKeys
import com.apollographql.apollo.api.Optional
import javax.inject.Inject

import com.tasneem.safwa.network.type.ProductSortKeys
import com.tasneem.safwa.network.type.LanguageCode

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val mapper: ProductMapper,
    private val searchMapper: SearchProductMapper,
    private val productDetailsDtoMapper: ProductDetailsDtoMapper
) : ProductRemoteDataSource {

    override suspend fun getProducts(first: Int, after: String?, sortKey: String?, reverse: Boolean?, languageCode: String): List<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    ProductsQuery(
                        first = first,
                        after = Optional.presentIfNotNull(after),
                        sortKey = Optional.presentIfNotNull(
                            sortKey?.let {
                                try { ProductSortKeys.valueOf(it) } catch (_: Exception) { null }
                            }
                        ),
                        reverse = Optional.presentIfNotNull(reverse),
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                val products = data.products.edges
                    .map { it.node }
                mapper.mapList(products)
            }
        )
    }

    override suspend fun getPaginatedProducts(
        first: Int,
        after: String?,
        sortKey: String?,
        reverse: Boolean?,
        languageCode: String
    ): PaginatedResultDto<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    ProductsQuery(
                        first = first,
                        after = Optional.presentIfNotNull(after),
                        sortKey = Optional.presentIfNotNull(
                            sortKey?.let {
                                try { ProductSortKeys.valueOf(it) } catch (_: Exception) { null }
                            }
                        ),
                        reverse = Optional.presentIfNotNull(reverse),
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                val products = data.products.edges
                    .map { it.node }
                PaginatedResultDto(
                    items = mapper.mapList(products),
                    endCursor = data.products.pageInfo.endCursor,
                    hasNextPage = data.products.pageInfo.hasNextPage
                )
            }
        )
    }

    override suspend fun searchProducts(query: String, first: Int, languageCode: String): List<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    SearchProductsQuery(
                        query = query,
                        first = first,
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                val products = data.products.edges
                    .map { it.node }
                searchMapper.mapList(products)
            }
        )
    }

    override suspend fun getProductDetailsByHandle(handle: String, languageCode: String): ProductDetailsDto {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    GetProductByHandleQuery(
                        handle = handle,
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                productDetailsDtoMapper.map(data)
            }
        )
    }

    override suspend fun getCategories(first: Int, languageCode: String): List<CategoryDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    GetCategoriesQuery(
                        first = first,
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                data.collections.edges.map { edge ->
                    val node = edge.node
                    CategoryDto(
                        id = node.id,
                        title = node.title,
                        handle = node.handle,
                        description = node.description,
                        imageUrl = node.image?.url?.toString(),
                        imageAlt = node.image?.altText
                    )
                }
            }
        )
    }

    override suspend fun getCollectionProducts(handle: String, first: Int, languageCode: String): List<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    GetCollectionProductsQuery(
                        handle = handle,
                        first = first,
                        sortKey = Optional.present(ProductCollectionSortKeys.PRICE),
                        reverse = Optional.present(false),
                        language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                    )
                ).execute()
            },
            mapper = { data ->
                data.collectionByHandle?.products?.edges?.map { edge ->
                    val node = edge.node
                    ProductDto(
                        id = node.id,
                        handle = node.handle,
                        title = node.title,
                        description = "",
                        vendor = node.vendor,
                        productType = handle,
                        price = node.priceRange.minVariantPrice.amount.toString(),
                        currency = node.priceRange.minVariantPrice.currencyCode.name,
                        imageUrls = node.images.edges.map { it.node.url.toString() },
                        imageAlts = node.images.edges.map { it.node.altText }
                    )
                } ?: emptyList()
            }
        )
    }
}