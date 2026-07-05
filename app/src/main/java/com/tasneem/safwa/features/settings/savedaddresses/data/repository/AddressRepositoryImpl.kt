package com.tasneem.safwa.features.settings.savedaddresses.data.repository

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.exception.ApolloException
import com.tasneem.network.datasource.address.CustomerAddressRemoteDataSource
import com.tasneem.safwa.features.settings.savedaddresses.data.local.AddressLocalDataSource
import com.tasneem.safwa.features.settings.savedaddresses.data.mapper.toCacheDto
import com.tasneem.safwa.features.settings.savedaddresses.data.mapper.toDomain
import com.tasneem.safwa.features.settings.savedaddresses.data.mapper.toInput
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressRepository
import com.tasneem.safwa.network.GetCustomerAddressesQuery
import java.io.IOException
import javax.inject.Inject

class ShopifyAddressException(message: String) : Exception(message)
class AddressRepositoryImpl @Inject constructor(
    private val remote: CustomerAddressRemoteDataSource,
    private val local: AddressLocalDataSource
) : AddressRepository {

    override suspend fun getAddresses(customerAccessToken: String): Result<List<Address>> {
        val response: ApolloResponse<GetCustomerAddressesQuery.Data> = remote.getAddresses(customerAccessToken)

        if (response.exception != null) {
            val cached = local.getCachedAddresses().map { it.toDomain() }
            return if (cached.isNotEmpty()) Result.success(cached) else Result.failure(response.exception!!)
        }

        return runCatching {
            if (response.hasErrors()) {
                throw ShopifyAddressException(response.errors?.firstOrNull()?.message ?: "Failed to load addresses.")
            }
            val customer = response.data?.customer
                ?: throw ShopifyAddressException("Session expired — please sign in again.")

            val defaultId = customer.defaultAddress?.id
            val addresses = customer.addresses.edges.map { edge ->
                edge.node.customerAddressFields.toDomain(isDefault = edge.node.customerAddressFields.id == defaultId)
            }
            local.cacheAddresses(addresses.map { it.toCacheDto() })
            addresses
        }
    }

    override suspend fun createAddress(customerAccessToken: String, address: Address): Result<Address> = runCatching {
        val response = remote.createAddress(customerAccessToken, address.toInput())
        response.exception?.let { throw it }
        val payload = response.data?.customerAddressCreate
            ?: throw ShopifyAddressException(response.errors?.firstOrNull()?.message ?: "Could not save address.")
        payload.customerUserErrors.firstOrNull()?.let { throw ShopifyAddressException(it.message) }
        val created = payload.customerAddress?.customerAddressFields
            ?: throw ShopifyAddressException("Shopify did not return the saved address.")

        val result = created.toDomain(isDefault = address.isDefault)
        if (address.isDefault) setDefaultAddress(customerAccessToken, result.id).getOrThrow()
        result
    }

    override suspend fun updateAddress(customerAccessToken: String, address: Address): Result<Address> = runCatching {
        require(address.id.isNotBlank()) { "Cannot update an address with no id." }
        val response = remote.updateAddress(customerAccessToken, address.id, address.toInput())
        response.exception?.let { throw it }
        val payload = response.data?.customerAddressUpdate
            ?: throw ShopifyAddressException(response.errors?.firstOrNull()?.message ?: "Could not update address.")
        payload.customerUserErrors.firstOrNull()?.let { throw ShopifyAddressException(it.message) }
        val updated = payload.customerAddress?.customerAddressFields
            ?: throw ShopifyAddressException("Shopify did not return the updated address.")

        val result = updated.toDomain(isDefault = address.isDefault)
        if (address.isDefault) setDefaultAddress(customerAccessToken, address.id).getOrThrow()
        result
    }

    override suspend fun deleteAddress(customerAccessToken: String, addressId: String): Result<Unit> = runCatching {
        val response = remote.deleteAddress(customerAccessToken, addressId)
        response.exception?.let { throw it }
        val payload = response.data?.customerAddressDelete
            ?: throw ShopifyAddressException(response.errors?.firstOrNull()?.message ?: "Could not delete address.")
        payload.customerUserErrors.firstOrNull()?.let { throw ShopifyAddressException(it.message) }
    }

    override suspend fun setDefaultAddress(customerAccessToken: String, addressId: String): Result<Unit> = runCatching {
        val response = remote.setDefaultAddress(customerAccessToken, addressId)
        response.exception?.let { throw it }
        val payload = response.data?.customerDefaultAddressUpdate
            ?: throw ShopifyAddressException(response.errors?.firstOrNull()?.message ?: "Could not set default address.")
        payload.customerUserErrors.firstOrNull()?.let { throw ShopifyAddressException(it.message) }
    }
}
