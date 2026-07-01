package com.tasneem.safwa.features.auth.domain.usecase
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GuestLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionPreferencesRepository: SessionPreferencesRepository
) {
    suspend operator fun invoke(): Resource<User> {
        val result = authRepository.loginAsGuest()
        if (result is Resource.Success) {
            sessionPreferencesRepository.saveUserSession(result.data)
        }
        return result
    }
}