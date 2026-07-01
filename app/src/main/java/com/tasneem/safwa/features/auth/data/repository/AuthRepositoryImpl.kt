package com.tasneem.safwa.features.auth.data.repository

import com.tasneem.safwa.core.data.mapper.toDomain
import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSource
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Resource<User> {
        return try {
            val authResult = authDataSource.signUpWithEmail(email, password)
            val uid = authResult.user?.uid ?: return Resource.Error("Registration failed: no user")

            val userEntity = UserEntity(
                id = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                isGuest = false
            )

            try {
                firestoreDataSource.saveUser(userEntity)
                authDataSource.sendEmailVerification()
            } catch (innerException: Exception) {

                authDataSource.signOut()
                throw innerException
            }

            authDataSource.signOut()
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val authResult = authDataSource.signInWithEmail(email, password)
            val user = authResult.user ?: return Resource.Error("Login failed")
            val uid = user.uid

            if (!user.isEmailVerified) {
                authDataSource.signOut()
                return Resource.Error("Please verify your email before logging in. A verification link was sent to your email.")
            }

            var userEntity = firestoreDataSource.getUser(uid)
            if (userEntity == null) {
                userEntity = UserEntity(
                    id = uid,
                    email = email,
                    isGuest = false
                )
                firestoreDataSource.saveUser(userEntity)
            }
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun sendVerificationEmail(): Resource<Unit> {
        return try {
            authDataSource.sendEmailVerification()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to send verification email")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return authDataSource.isEmailVerified()
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val authResult = authDataSource.signInWithGoogle(idToken)
            val uid = authResult.user?.uid ?: return Resource.Error("Google login failed")
            var userEntity = firestoreDataSource.getUser(uid)
            if (userEntity == null) {
                val email = authResult.user?.email ?: ""
                val displayName = authResult.user?.displayName ?: ""
                val firstName = displayName.split(" ").firstOrNull() ?: ""
                val lastName = displayName.split(" ").drop(1).joinToString(" ")
                userEntity = UserEntity(
                    id = uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = authResult.user?.photoUrl?.toString(),
                    isGuest = false
                )
                firestoreDataSource.saveUser(userEntity)
            }
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun loginAsGuest(): Resource<User> {
        return try {
            val authResult = authDataSource.signInAnonymously()
            val uid = authResult.user?.uid ?: return Resource.Error("Guest login failed")
            val guestUser = User(
                id = uid,
                isGuest = true,
                firstName = "Guest",
                lastName = "",
                email = null
            )
            Resource.Success(guestUser)
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            authDataSource.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Logout failed")
        }
    }

    override suspend fun getCurrentUser(): Resource<User?> {
        return try {
            val uid = authDataSource.getCurrentUserId() ?: return Resource.Success(null)
            val email = authDataSource.getCurrentUserEmail()

            val userEntity = firestoreDataSource.getUser(uid)

            if (userEntity != null) {
                // User exists in Firestore. Ensure email is verified if they have one.
                if (email != null && !authDataSource.isEmailVerified()) {
                    authDataSource.signOut()
                    return Resource.Success(null)
                }
                return Resource.Success(userEntity.toDomain())
            } else {
                // User is in Firebase Auth but NOT in Firestore
                if (email == null) {
                    // It's a Guest User (they don't have emails and aren't saved to Firestore)
                    val guestUser = User(
                        id = uid,
                        isGuest = true,
                        firstName = "Guest",
                        lastName = "",
                        email = null
                    )
                    return Resource.Success(guestUser)
                } else {
                    // CRITICAL FIX: It's an Email user who encountered an error during registration
                    // and their Firestore data never saved. Clean up the corrupted local state.
                    authDataSource.signOut()
                    return Resource.Success(null)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to get user")
        }
    }

    override fun observeAuthState(): Flow<AuthState> = authDataSource.observeAuthState()
        .map { isSignedIn ->
            if (!isSignedIn) {
                AuthState.Unauthenticated
            } else {
                val uid = authDataSource.getCurrentUserId()
                    ?: return@map AuthState.Unauthenticated
                val email = authDataSource.getCurrentUserEmail()

                if (email != null && !authDataSource.isEmailVerified()) {
                    authDataSource.signOut()
                    return@map AuthState.Unauthenticated
                }

                val userEntity = firestoreDataSource.getUser(uid)
                if (userEntity != null) {
                    AuthState.Authenticated(userEntity.toDomain())
                } else {
                    authDataSource.signOut()
                    AuthState.Unauthenticated
                }
            }
        }
        .catch { emit(AuthState.Unauthenticated) }

    private fun handleAuthException(e: Exception): Resource<User> {
        return when (e) {
            is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                Resource.Error("Password too weak")
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                Resource.Error("Invalid credentials")
            is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                Resource.Error("Email already in use")
            else -> Resource.Error(e.localizedMessage ?: "Authentication error")
        }
    }
}


