package com.tasneem.safwa.features.auth.data.datasource.auth

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthDataSource {
    suspend fun signUpWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun signInAnonymously(): AuthResult
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    suspend fun sendEmailVerification(user: com.google.firebase.auth.FirebaseUser? = null)
    fun isEmailVerified(): Boolean
    fun observeAuthState(): Flow<Boolean>
}