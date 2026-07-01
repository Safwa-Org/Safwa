package com.tasneem.safwa.core.domain.usecase.preferences

import com.tasneem.safwa.core.domain.model.Address
import com.tasneem.safwa.core.domain.model.AppPreferences
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressFieldError
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressValidationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    operator fun invoke(): Flow<User?> = repo.currentUser
}
class SaveUserSessionUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    suspend operator fun invoke(user: User) = repo.saveUserSession(user)
}
class LogoutUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    suspend operator fun invoke() = repo.clearSession()
}
class GetAppPreferencesUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    operator fun invoke(): Flow<AppPreferences> = repo.appPreferences
}
class UpdateAppPreferencesUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    suspend fun updateTheme(isDark: Boolean) = repo.updateTheme(isDark)
    suspend fun updateLanguage(lang: String) = repo.updateLanguage(lang)
    suspend fun updateCurrency(currency: String) = repo.updateCurrency(currency)
}
class GetSavedAddressesUseCase @Inject constructor(
    private val repo: SessionPreferencesRepository
) {
    operator fun invoke(): Flow<List<Address>> {
        return repo.currentUser.map { user ->
            user?.addresses ?: emptyList()
        }
    }
}

class UpdateAddressUseCase @Inject constructor(
    private val localRepo: SessionPreferencesRepository,
    private val remoteRepo: RemoteUserRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    // Enforces accurate Egyptian mobile carrier structure (11 digits total)
    private val phoneRegex = Regex("^(010|011|012|015)[0-9]{8}$")

    suspend operator fun invoke(updatedAddress: Address): Result<Unit> {
        val errors = mutableListOf<AddressFieldError>()

        // 1. Validate Recipient Name
        if (updatedAddress.recipientName.isBlank()) {
            errors.add(AddressFieldError.RECIPIENT_NAME_EMPTY)
        }

        // 2. Validate Mobile Number using your exact regex pattern
        if (!phoneRegex.matches(updatedAddress.mobileNumber)) {
            errors.add(AddressFieldError.MOBILE_NUMBER_INVALID)
        }

        // 3. Validate Street
        if (updatedAddress.street.isBlank()) {
            errors.add(AddressFieldError.STREET_EMPTY)
        }

        // Break early if any of the domain rules failed
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

        // Return immediately
        return Result.success(Unit)
    }
}
class DeleteAddressUseCase @Inject constructor(
    private val localRepo: SessionPreferencesRepository,
    private val remoteRepo: RemoteUserRepository
) {
    suspend operator fun invoke(addressId: String): Result<Unit> {
        val currentUser = localRepo.currentUser.first()
            ?: return Result.failure(IllegalStateException("User session not found."))

        val updatedAddresses = currentUser.addresses.filterNot { it.id == addressId }
        val updatedUser = currentUser.copy(addresses = updatedAddresses)

        localRepo.saveUserSession(updatedUser)

        return try {
            remoteRepo.uploadUser(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}