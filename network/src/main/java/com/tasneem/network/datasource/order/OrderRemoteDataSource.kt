package com.tasneem.network.datasource.order

import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.tasneem.safwa.network.GetOrdersQuery
import javax.inject.Inject

interface OrderRemoteDataSource {
    suspend fun getOrders(customerAccessToken: String): List<GetOrdersQuery.Edge>
}

class OrderRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : OrderRemoteDataSource {
    override suspend fun getOrders(customerAccessToken: String): List<GetOrdersQuery.Edge> {
        val response = apolloClient
            .query(GetOrdersQuery(customerAccessToken))
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Failed to fetch orders")
        }

        return response.data?.customer?.orders?.edges?.filterNotNull() ?: emptyList()
    }
}
