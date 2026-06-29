package com.tasneem.safwa.features.auth.domain.usecase
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Resource<User?> {
        return authRepository.getCurrentUser()
    }
}