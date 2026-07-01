package com.tasneem.safwa.core.domain.repository

import com.tasneem.safwa.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RemoteUserRepository {
    fun observeUser(userId: String): Flow<User?>
    suspend fun uploadUser(user: User)
}