package com.tasneem.safwa.features.auth.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.tasneem.safwa.core.data.mapper.toDomain
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSource
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

import com.tasneem.network.datasource.auth.AuthRemoteDataSource

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Resource<User> {
        return try {
            val guest = guestSnapshotOrNull()
            var linkedFromGuest = false
            val authResult = if (guest != null) {
                val result = authDataSource.linkWithCredential(
                    EmailAuthProvider.getCredential(email, password)
                )
                linkedFromGuest = true
                result
            } else {
                authDataSource.signUpWithEmail(email, password)
            }
            val uid = authResult.user?.uid ?: return Resource.Error("Registration failed: no user")

            val shopifyPassword = uid.take(10) + "Safwa123!"
            val shopifyResult = authRemoteDataSource.registerCustomer(email, shopifyPassword, firstName, lastName, phone)
            if (shopifyResult.isFailure) {
                rollbackUpgrade(linkedFromGuest, EmailAuthProvider.PROVIDER_ID)
                return Resource.Error("Shopify registration failed: ${shopifyResult.exceptionOrNull()?.message}")
            }
            val shopifyToken = shopifyResult.getOrNull()

            val userEntity = UserEntity(
                id = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                isGuest = false,
                cartId = guest?.cartId ?: "",
                customerAccessToken = shopifyToken
            )

            try {
                firestoreDataSource.saveUser(userEntity)
            } catch (innerException: Exception) {
                android.util.Log.e("AuthRepository", "Failed to save user to Firestore", innerException)
                rollbackUpgrade(linkedFromGuest, EmailAuthProvider.PROVIDER_ID)
                throw innerException
            }

            try {
                authDataSource.sendEmailVerification(authResult.user)
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Failed to send verification email during registration", e)
            }

            authDataSource.signOut()
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val guest = guestSnapshotOrNull()
            val authResult = authDataSource.signInWithEmail(email, password)
            val user = authResult.user ?: return Resource.Error("Login failed")
            val uid = user.uid

            if (!user.isEmailVerified) {
                authDataSource.signOut()
                return Resource.Error("Please verify your email before logging in. A verification link was sent to your email.")
            }

            val deterministicPassword = uid.take(10) + "Safwa123!"
            var shopifyResult = authRemoteDataSource.loginCustomer(email, deterministicPassword)

            if (shopifyResult.isFailure && shopifyResult.exceptionOrNull()?.message?.contains("Unidentified customer", ignoreCase = true) == true) {
                val fallbackResult = authRemoteDataSource.loginCustomer(email, password)
                if (fallbackResult.isSuccess) {
                    val token = fallbackResult.getOrNull()
                    if (token != null) {
                        authRemoteDataSource.updateCustomerPassword(token, deterministicPassword)
                        shopifyResult = Result.success(token)
                    }
                }
            }

            if (shopifyResult.isFailure) {
                val errorMsg = shopifyResult.exceptionOrNull()?.message ?: ""
                authDataSource.signOut()
                return Resource.Error("Shopify login failed: $errorMsg")
            }
            val shopifyToken = shopifyResult.getOrNull()

            val existing = firestoreDataSource.getUser(uid)
            val userEntity = existing?.copy(
                customerAccessToken = shopifyToken,
                cartId = existing.cartId.ifEmpty { guest?.cartId ?: "" }
            )
                ?: UserEntity(
                    id = uid,
                    email = email,
                    isGuest = false,
                    cartId = guest?.cartId ?: "",
                    customerAccessToken = shopifyToken
                )
            firestoreDataSource.saveUser(userEntity)
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
            android.util.Log.e("AuthRepository", "Failed to send verification email", e)
            Resource.Error(e.localizedMessage ?: "Failed to send verification email")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return authDataSource.isEmailVerified()
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val guest = guestSnapshotOrNull()
            var linkedFromGuest = false
            val authResult = if (guest != null) {
                try {
                    val result = authDataSource.linkWithCredential(
                        GoogleAuthProvider.getCredential(idToken, null)
                    )
                    linkedFromGuest = true
                    result
                } catch (e: FirebaseAuthUserCollisionException) {
                    authDataSource.signInWithGoogle(idToken)
                }
            } else {
                authDataSource.signInWithGoogle(idToken)
            }
            val uid = authResult.user?.uid ?: return Resource.Error("Google login failed")

            val email = authResult.user?.email ?: ""
            val displayName = authResult.user?.displayName ?: ""
            val firstName = displayName.split(" ").firstOrNull() ?: ""
            val lastName = displayName.split(" ").drop(1).joinToString(" ")

            val shopifyPassword = uid.take(10) + "Safwa123!"

            var shopifyResult = authRemoteDataSource.loginCustomer(email, shopifyPassword)

            if (shopifyResult.isFailure) {
                shopifyResult = authRemoteDataSource.registerCustomer(email, shopifyPassword, firstName, lastName, null)
                if (shopifyResult.isFailure) {
                     rollbackUpgrade(linkedFromGuest, GoogleAuthProvider.PROVIDER_ID)
                     val msg = shopifyResult.exceptionOrNull()?.message ?: ""
                     if (msg.contains("Email has already been taken", ignoreCase = true) ||
                         msg.contains("has already been taken", ignoreCase = true)
                     ) {
                         return Resource.Error("This email is already registered. Please login using Email and Password.")
                     }
                     if (msg.contains("We have sent an email", ignoreCase = true) || msg.contains("verify", ignoreCase = true)) {
                         return Resource.Error("Please check your email inbox and verify your email address to continue.")
                     }
                     return Resource.Error("Shopify error: $msg")
                }
            }

            val shopifyToken = shopifyResult.getOrNull()

            val existing = firestoreDataSource.getUser(uid)
            val resolvedCartId = existing?.cartId?.ifEmpty { guest?.cartId ?: "" }
                ?: guest?.cartId ?: ""
            val userEntity = if (existing == null || existing.isGuest) {
                UserEntity(
                    id = uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = authResult.user?.photoUrl?.toString(),
                    isGuest = false,
                    cartId = resolvedCartId,
                    customerAccessToken = shopifyToken
                )
            } else {
                existing.copy(customerAccessToken = shopifyToken, cartId = resolvedCartId)
            }
            firestoreDataSource.saveUser(userEntity)
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            handleAuthException(e)
        }
    }

    override suspend fun getCurrentUser(): Resource<User?> {
        return try {
            val uid = authDataSource.getCurrentUserId() ?: return Resource.Success(null)
            val entity = firestoreDataSource.getUser(uid)

            if (authDataSource.isAnonymous()) {
                Resource.Success(
                    entity?.toDomain()?.copy(isGuest = true)
                        ?: User(id = uid, firstName = "Guest", isGuest = true)
                )
            } else {
                Resource.Success(
                    entity?.toDomain()?.copy(isGuest = false)
                        ?: User(id = uid, email = authDataSource.getCurrentUserEmail())
                )
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to get user")
        }
    }

    override suspend fun updateCartId(cartId: String): Resource<Unit> {
        return try {
            val uid = authDataSource.getCurrentUserId() ?: return Resource.Error("User not logged in")
            firestoreDataSource.updateUser(uid, mapOf("cartId" to cartId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update cart ID")
        }
    }

    private suspend fun guestSnapshotOrNull(): User? {
        val uid = authDataSource.getCurrentUserId() ?: return null
        if (!authDataSource.isAnonymous()) return null
        return try {
            firestoreDataSource.getUser(uid)?.toDomain()?.copy(isGuest = true)
                ?: User(id = uid, firstName = "Guest", isGuest = true)
        } catch (e: Exception) {
            User(id = uid, firstName = "Guest", isGuest = true)
        }
    }

    private suspend fun rollbackUpgrade(linkedFromGuest: Boolean, providerId: String) {
        if (linkedFromGuest) {
            runCatching { authDataSource.unlinkProvider(providerId) }
                .onFailure { android.util.Log.e("AuthRepository", "Failed to unlink $providerId", it) }
        } else {
            runCatching { authDataSource.signOut() }
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
