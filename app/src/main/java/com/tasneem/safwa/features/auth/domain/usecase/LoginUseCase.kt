package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return Resource.Error("error_email_empty")
        }
        if (password.isBlank()) {
            return Resource.Error("error_password_empty")
        }

        return authRepository.login(trimmedEmail, password)
    }
}