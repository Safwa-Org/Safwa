package com.tasneem.safwa.features.auth.domain.session

import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import kotlinx.coroutines.flow.StateFlow

/**
 * single source of truth for the authentication session.
 */
interface AuthSessionManager {

    val authState: StateFlow<AuthState>

    fun start()

    suspend fun onAuthenticated(user: User)

    suspend fun signOutToGuest()

    suspend fun ensureGuestSession(): Resource<User>
}
