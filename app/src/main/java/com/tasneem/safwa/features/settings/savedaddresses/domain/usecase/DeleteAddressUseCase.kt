package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

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