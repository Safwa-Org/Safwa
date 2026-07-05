package com.tasneem.safwa.features.settings.savedaddresses.domain.usecase

import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSavedAddressesUseCase @Inject constructor(
    private val repo: SessionPreferencesRepository
) {
    operator fun invoke(): Flow<List<Address>> {
        return repo.currentUser.map { user ->
            user?.addresses ?: emptyList()
        }
    }
}