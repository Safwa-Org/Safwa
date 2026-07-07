package com.tasneem.network.datasource.address

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.tasneem.safwa.network.CustomerAddressCreateMutation
import com.tasneem.safwa.network.CustomerAddressDeleteMutation
import com.tasneem.safwa.network.CustomerAddressUpdateMutation
import com.tasneem.safwa.network.CustomerDefaultAddressUpdateMutation
import com.tasneem.safwa.network.GetCustomerAddressesQuery
import com.tasneem.safwa.network.type.MailingAddressInput
import javax.inject.Inject

interface CustomerAddressRemoteDataSource {
    suspend fun getAddresses(customerAccessToken: String): ApolloResponse<GetCustomerAddressesQuery.Data>
    suspend fun createAddress(customerAccessToken: String, address: MailingAddressInput): ApolloResponse<CustomerAddressCreateMutation.Data>
    suspend fun updateAddress(customerAccessToken: String, id: String, address: MailingAddressInput): ApolloResponse<CustomerAddressUpdateMutation.Data>
    suspend fun deleteAddress(customerAccessToken: String, id: String): ApolloResponse<CustomerAddressDeleteMutation.Data>
    suspend fun setDefaultAddress(customerAccessToken: String, addressId: String): ApolloResponse<CustomerDefaultAddressUpdateMutation.Data>
}

class CustomerAddressRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : CustomerAddressRemoteDataSource {

    override suspend fun getAddresses(customerAccessToken: String) =
        apolloClient.query(GetCustomerAddressesQuery(customerAccessToken))
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()
    override suspend fun createAddress(customerAccessToken: String, address: MailingAddressInput) =
        apolloClient.mutation(CustomerAddressCreateMutation(customerAccessToken, address)).execute()

    override suspend fun updateAddress(customerAccessToken: String, id: String, address: MailingAddressInput) =
        apolloClient.mutation(CustomerAddressUpdateMutation(customerAccessToken, id, address)).execute()

    override suspend fun deleteAddress(customerAccessToken: String, id: String) =
        apolloClient.mutation(CustomerAddressDeleteMutation(customerAccessToken, id)).execute()

    override suspend fun setDefaultAddress(customerAccessToken: String, addressId: String) =
        apolloClient.mutation(CustomerDefaultAddressUpdateMutation(customerAccessToken, addressId)).execute()
}