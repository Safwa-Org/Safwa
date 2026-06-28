package com.tasneem.network.datasource.product

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.safeApiCall
import com.tasneem.network.mapper.ProductMapper
import com.tasneem.network.mapper.mapList
import com.tasneem.safwa.network.ProductsQuery
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val mapper: ProductMapper
) : ProductRemoteDataSource {

    override suspend fun getProducts(page: Int): List<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    ProductsQuery(first = page)
                ).execute()
            },
            mapper = { data ->
                val products = data.products.edges
                    .map { it.node }
                mapper.mapList(products)
            }
        )
    }
}