package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SyncUserSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) {
    suspend operator fun invoke() {
        when (val firebaseUserResult = authRepository.getCurrentUser()) {
            is Resource.Success -> {
                val firebaseUser = firebaseUserResult.data
                if (firebaseUser != null) {
                    sessionPreferencesRepository.saveUserSession(firebaseUser)
                } else {
                    // Firebase session is expired or invalid, clear local preferences
                    sessionPreferencesRepository.clearSession()
                }
            }
            is Resource.Error -> {
                // Network error or timeout. We rely on the local DataStore cache for offline capability.
                // Do nothing to local preferences here.
            }
            else -> {}
        }
    }
}