package com.tasneem.safwa.features.auth.domain.repository

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User

interface AuthRepository {

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Resource<User>

    suspend fun login(email: String, password: String): Resource<User>

    suspend fun loginWithGoogle(idToken: String): Resource<User>

    suspend fun loginAsGuest(): Resource<User>

    suspend fun logout(): Resource<Unit>

    suspend fun getCurrentUser(): Resource<User?>
}
