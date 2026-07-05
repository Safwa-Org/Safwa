package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressFieldError
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressValidationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(
    private val localRepo: SessionPreferencesRepository,
    private val remoteRepo: RemoteUserRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    private val phoneRegex = Regex("^(010|011|012|015)[0-9]{8}$")

    suspend operator fun invoke(updatedAddress: Address): Result<Unit> {
        val errors = mutableListOf<AddressFieldError>()

        if (updatedAddress.recipientName.isBlank()) {
            errors.add(AddressFieldError.RECIPIENT_NAME_EMPTY)
        }

        if (!phoneRegex.matches(updatedAddress.mobileNumber)) {
            errors.add(AddressFieldError.MOBILE_NUMBER_INVALID)
        }

        if (!updatedAddress.isValidated || updatedAddress.latitude == null || updatedAddress.longitude == null) {
            errors.add(AddressFieldError.ADDRESS_NOT_SELECTED)
        }

        if (errors.isNotEmpty()) {
            return Result.failure(AddressValidationException(errors))
        }

        val currentUser = localRepo.currentUser.first()
            ?: return Result.failure(IllegalStateException("User not logged in."))

        val currentList = currentUser.addresses.toMutableList()
        val addressToSave = if (updatedAddress.id.isEmpty()) {
            updatedAddress.copy(id = UUID.randomUUID().toString())
        } else {
            updatedAddress
        }

        val index = currentList.indexOfFirst { it.id == addressToSave.id }
        if (index != -1) currentList[index] = addressToSave else currentList.add(addressToSave)

        val updatedUser = currentUser.copy(addresses = currentList)
        localRepo.saveUserSession(updatedUser)

        externalScope.launch {
            try {
                remoteRepo.uploadUser(updatedUser)
            } catch (e: Exception) {
                // Log the error silently; the local save was successful.
            }
        }

        return Result.success(Unit)
    }
}