package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.session.AuthSessionManager
import javax.inject.Inject

class GuestLoginUseCase @Inject constructor(
    private val authSessionManager: AuthSessionManager
) {
    suspend operator fun invoke(): Resource<User> = authSessionManager.ensureGuestSession()
}
