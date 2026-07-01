package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthState> = authRepository.observeAuthState()
}
