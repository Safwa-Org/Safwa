package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.features.auth.domain.session.AuthSessionManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authSessionManager: AuthSessionManager,
) {
    operator fun invoke(): StateFlow<AuthState> = authSessionManager.authState
}
