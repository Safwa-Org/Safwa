package com.tasneem.safwa.features.auth.data.repository

import android.util.Log
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSource
import com.tasneem.safwa.features.auth.data.model.UserEntity
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource
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
            firestoreDataSource.saveUser(userEntity)
            Log.d("MustDelete", userEntity.toDomain().toString())
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val authResult = authDataSource.signInWithEmail(email, password)
            val uid = authResult.user?.uid ?: return Resource.Error("Login failed")
            var userEntity = firestoreDataSource.getUser(uid)
            if (userEntity == null) {
                // If no Firestore document, create one with minimal data
                userEntity = UserEntity(
                    id = uid,
                    email = email,
                    isGuest = false
                )
                firestoreDataSource.saveUser(userEntity)
            }
            Log.d("MustDelete", userEntity.toDomain().toString())

            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
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
            Log.d("MustDelete", userEntity.toDomain().toString())

            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun loginAsGuest(): Resource<User> {
        return try {
            val authResult = authDataSource.signInAnonymously()
            val uid = authResult.user?.uid ?: return Resource.Error("Guest login failed")
            // Guest user is not saved to Firestore, just return domain object
            val guestUser = User(
                id = uid,
                isGuest = true,
                firstName = "Guest",
                lastName = "",
                email = null
            )
            Log.d("MustDelete",guestUser.toString())

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
            val userEntity = firestoreDataSource.getUser(uid)
            Resource.Success(userEntity?.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to get user")
        }
    }

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

private fun UserEntity.toDomain(): User {
    return User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        photoUrl = photoUrl,
        isGuest = isGuest,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}