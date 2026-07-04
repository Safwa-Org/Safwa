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
            val authResult = authDataSource.signUpWithEmail(email, password)
            val uid = authResult.user?.uid ?: return Resource.Error("Registration failed: no user")

            val shopifyPassword = uid.take(10) + "Safwa123!"
            val shopifyResult = authRemoteDataSource.registerCustomer(email, shopifyPassword, firstName, lastName, phone)
            if (shopifyResult.isFailure) {
                // If Shopify registration fails, clean up Firebase user and return error
                authDataSource.signOut()
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
                customerAccessToken = shopifyToken
            )

            try {
                firestoreDataSource.saveUser(userEntity)
            } catch (innerException: Exception) {
                android.util.Log.e("AuthRepository", "Failed to save user to Firestore", innerException)
                authDataSource.signOut()
                throw innerException
            }

            try {
                authDataSource.sendEmailVerification(authResult.user)
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Failed to send verification email during registration", e)
                // Registration succeeded even if email sending fails, do not throw
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

            val deterministicPassword = uid.take(10) + "Safwa123!"
            var shopifyResult = authRemoteDataSource.loginCustomer(email, deterministicPassword)
            
            // Fallback for older users who might have their actual password registered
            if (shopifyResult.isFailure && shopifyResult.exceptionOrNull()?.message?.contains("Unidentified customer", ignoreCase = true) == true) {
                val fallbackResult = authRemoteDataSource.loginCustomer(email, password)
                if (fallbackResult.isSuccess) {
                    val token = fallbackResult.getOrNull()
                    if (token != null) {
                        // Silently migrate their Shopify password to the deterministic one
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

            var userEntity = firestoreDataSource.getUser(uid)
            if (userEntity == null) {
                userEntity = UserEntity(
                    id = uid,
                    email = email,
                    isGuest = false,
                    customerAccessToken = shopifyToken
                )
                firestoreDataSource.saveUser(userEntity)
            } else {
                userEntity = userEntity.copy(customerAccessToken = shopifyToken)
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
            android.util.Log.e("AuthRepository", "Failed to send verification email", e)
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
            
            val email = authResult.user?.email ?: ""
            val displayName = authResult.user?.displayName ?: ""
            val firstName = displayName.split(" ").firstOrNull() ?: ""
            val lastName = displayName.split(" ").drop(1).joinToString(" ")
            
            val shopifyPassword = uid.take(10) + "Safwa123!"

            var shopifyResult = authRemoteDataSource.loginCustomer(email, shopifyPassword)
            
            if (shopifyResult.isFailure) {
                shopifyResult = authRemoteDataSource.registerCustomer(email, shopifyPassword, firstName, lastName, null)
                if (shopifyResult.isFailure) {
                     authDataSource.signOut()
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
            } else {
                // If loginCustomer succeeded, but we were checking for "We have sent an email"
                // wait, if loginCustomer succeeded, shopifyResult is success.
            }
            
            val shopifyToken = shopifyResult.getOrNull()

            var userEntity = firestoreDataSource.getUser(uid)
            if (userEntity == null) {
                userEntity = UserEntity(
                    id = uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = authResult.user?.photoUrl?.toString(),
                    isGuest = false,
                    customerAccessToken = shopifyToken
                )
                firestoreDataSource.saveUser(userEntity)
            } else {
                userEntity = userEntity.copy(customerAccessToken = shopifyToken)
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
                if (email != null && !authDataSource.isEmailVerified()) {
                    authDataSource.signOut()
                    return Resource.Success(null)
                }
                return Resource.Success(userEntity.toDomain())
            } else {
                if (email == null) {
                    val guestUser = User(
                        id = uid,
                        isGuest = true,
                        firstName = "Guest",
                        lastName = "",
                        email = null
                    )
                    return Resource.Success(guestUser)
                } else {
                    authDataSource.signOut()
                    return Resource.Success(null)
                }
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


