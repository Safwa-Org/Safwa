package com.tasneem.safwa.features.settings.savedaddresses.domain.repository

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address

interface AddressRepository {
    suspend fun getAddresses(customerAccessToken: String): Result<List<Address>>
    suspend fun createAddress(customerAccessToken: String, address: Address): Result<Address>
    suspend fun updateAddress(customerAccessToken: String, address: Address): Result<Address>
    suspend fun deleteAddress(customerAccessToken: String, addressId: String): Result<Unit>
    suspend fun setDefaultAddress(customerAccessToken: String, addressId: String): Result<Unit>
}