package com.tasneem.network.datasource.brand

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.safeApiCall
import com.tasneem.network.mapper.VendorProductMapper
import com.tasneem.network.mapper.mapList
import com.tasneem.safwa.network.GetProductVendorsQuery
import com.tasneem.safwa.network.GetProductsForVendorQuery
import com.tasneem.safwa.network.type.LanguageCode
import javax.inject.Inject

class BrandRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val vendorProductMapper: VendorProductMapper
) : BrandRemoteDataSource {

    override suspend fun getProductVendors(pageSize: Int, languageCode: String): List<String> {
        return fetchAllPages { after ->
            safeApiCall(
                apiCall = {
                    apolloClient.query(
                        GetProductVendorsQuery(
                            first = pageSize,
                            after = Optional.presentIfNotNull(after),
                            language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                        )
                    ).execute()
                },
                mapper = { data ->
                    Page(
                        items = data.products.nodes.map { it.vendor },
                        hasNextPage = data.products.pageInfo.hasNextPage,
                        endCursor = data.products.pageInfo.endCursor
                    )
                }
            )
        }
    }

    override suspend fun getProductsForVendor(vendor: String, pageSize: Int, languageCode: String): List<ProductDto> {
        return fetchAllPages { after ->
            safeApiCall(
                apiCall = {
                    apolloClient.query(
                        GetProductsForVendorQuery(
                            first = pageSize,
                            after = Optional.presentIfNotNull(after),
                            query = Optional.present(buildVendorQuery(vendor)),
                            language = Optional.presentIfNotNull(LanguageCode.safeValueOf(languageCode.uppercase()))
                        )
                    ).execute()
                },
                mapper = { data ->
                    Page(
                        items = vendorProductMapper.mapList(data.products.edges.map { it.node }),
                        hasNextPage = data.products.pageInfo.hasNextPage,
                        endCursor = data.products.pageInfo.endCursor
                    )
                }
            )
        }
    }

    private suspend fun <T> fetchAllPages(fetchPage: suspend (after: String?) -> Page<T>): List<T> {
        val items = mutableListOf<T>()
        var after: String? = null
        var hasNextPage: Boolean

        do {
            val page = fetchPage(after)
            items += page.items
            after = page.endCursor
            hasNextPage = page.hasNextPage
        } while (hasNextPage && after != null)

        return items
    }

    private fun buildVendorQuery(vendor: String): String =
        "vendor:\"${vendor.replace("\"", "\\\"")}\""

    private data class Page<T>(
        val items: List<T>,
        val hasNextPage: Boolean,
        val endCursor: String?
    )
}
