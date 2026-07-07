package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import com.tasneem.safwa.features.auth.domain.session.AuthSessionManager
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authSessionManager: AuthSessionManager
) {
    suspend operator fun invoke(idToken: String): Resource<User> {
        val result = authRepository.loginWithGoogle(idToken)
        if (result is Resource.Success) {
            authSessionManager.onAuthenticated(result.data)
        }
        return result
    }
}
