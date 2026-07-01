package com.tasneem.network.datasource.product

import com.apollographql.apollo.ApolloClient
import com.tasneem.network.dto.ProductDetailsDto
import com.tasneem.network.dto.ProductDto
import com.tasneem.network.exception.safeApiCall
import com.tasneem.network.mapper.ProductDetailsDtoMapper
import com.tasneem.network.mapper.ProductMapper
import com.tasneem.network.mapper.mapList
import com.tasneem.network.mapper.SearchProductMapper
import com.tasneem.safwa.network.GetProductByHandleQuery
import com.tasneem.safwa.network.ProductsQuery
import com.tasneem.safwa.network.SearchProductsQuery
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val mapper: ProductMapper,
    private val searchMapper: SearchProductMapper,
    private val productDetailsDtoMapper: ProductDetailsDtoMapper
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

    override suspend fun searchProducts(query: String, first: Int): List<ProductDto> {
        return safeApiCall(
            apiCall = {
                apolloClient.query(
                    SearchProductsQuery(query = query, first = first)
                ).execute()
            },
            mapper = { data ->
                val products = data.products.edges
                    .map { it.node }
                searchMapper.mapList(products)
            }
        )
    }

    override suspend fun getProductDetailsByHandle(handle: String): ProductDetailsDto {
        return safeApiCall(
            apiCall = {
                apolloClient.query(GetProductByHandleQuery(handle = handle)).execute()
            },
            mapper = { data ->
                productDetailsDtoMapper.map(data)
            }
        )
    }

}