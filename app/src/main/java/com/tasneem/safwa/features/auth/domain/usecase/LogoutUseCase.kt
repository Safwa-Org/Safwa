package com.tasneem.safwa.features.auth.domain.usecase
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.core.domain.usecase.ClearWishlistUseCase
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val clearWishlistUseCase: ClearWishlistUseCase,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        val result = authRepository.logout()
        if (result is Resource.Success) {
            clearWishlistUseCase()
            sessionPreferencesRepository.clearSession()
        }

        return result
    }
}