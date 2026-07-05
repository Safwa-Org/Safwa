package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.repository.AddressRepository
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(private val repo: AddressRepository) {
    suspend operator fun invoke(customerAccessToken: String): Result<List<Address>> =
        repo.getAddresses(customerAccessToken)
}

class SaveAddressUseCase @Inject constructor(private val repo: AddressRepository) {
    suspend operator fun invoke(customerAccessToken: String, address: Address): Result<Address> =
        if (address.id.isBlank()) {
            repo.createAddress(customerAccessToken, address)
        } else {
            repo.updateAddress(customerAccessToken, address)
        }
}

class DeleteAddressUseCase @Inject constructor(private val repo: AddressRepository) {
    suspend operator fun invoke(customerAccessToken: String, addressId: String): Result<Unit> =
        repo.deleteAddress(customerAccessToken, addressId)
}

class SetDefaultAddressUseCase @Inject constructor(private val repo: AddressRepository) {
    suspend operator fun invoke(customerAccessToken: String, addressId: String): Result<Unit> =
        repo.setDefaultAddress(customerAccessToken, addressId)
}