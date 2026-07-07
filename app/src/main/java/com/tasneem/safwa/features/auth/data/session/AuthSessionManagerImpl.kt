package com.tasneem.safwa.features.auth.data.session

import android.util.Log
import com.tasneem.safwa.core.data.mapper.toDomain
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.data.datasource.auth.FirebaseAuthDataSource
import com.tasneem.safwa.features.auth.data.datasource.firestore.FirestoreDataSource
import com.tasneem.safwa.features.auth.domain.session.AuthSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManagerImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val sessionPreferencesRepository: SessionPreferencesRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) : AuthSessionManager {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val transitionMutex = Mutex()
    private val started = AtomicBoolean(false)

    override fun start() {
        if (!started.compareAndSet(false, true)) return
        externalScope.launch {
            transitionMutex.withLock { reconcile(bootstrap = true) }

            authDataSource.observeAuthState().collect {
                transitionMutex.withLock { reconcile(bootstrap = false) }
            }
        }
    }

    override suspend fun onAuthenticated(user: User) = transitionMutex.withLock {
        setState(AuthState.Authenticated(user.copy(isGuest = false)))
    }

    override suspend fun signOutToGuest(): Unit = transitionMutex.withLock {
        runCatching { authDataSource.signOut() }
            .onFailure { Log.e(TAG, "Sign out failed", it) }
        establishGuestSession()
    }

    override suspend fun ensureGuestSession(): Resource<User> = transitionMutex.withLock {
        when (val current = _authState.value) {
            is AuthState.Guest -> if (current.user.id.isNotEmpty()) {
                return@withLock Resource.Success(current.user)
            } else Unit
            is AuthState.Authenticated -> return@withLock Resource.Success(current.user)
            AuthState.Loading -> Unit
        }
        try {
            val uid = authDataSource.getCurrentUserId()
                ?: authDataSource.signInAnonymously().user?.uid
                ?: return@withLock Resource.Error("Could not start a guest session")
            val user = loadOrCreateGuestUser(uid)
            setState(AuthState.Guest(user))
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Guest sign-in failed", e)
            Resource.Error(e.localizedMessage ?: "Could not start a guest session")
        }
    }

    private suspend fun reconcile(bootstrap: Boolean) {
        val uid = authDataSource.getCurrentUserId()
        if (uid == null) {
            establishGuestSession()
            return
        }

        if (authDataSource.isAnonymous()) {
            setState(AuthState.Guest(loadOrCreateGuestUser(uid)))
            return
        }

        val email = authDataSource.getCurrentUserEmail()
        if (email != null && !authDataSource.isEmailVerified()) {
            if (bootstrap) {
                runCatching { authDataSource.signOut() }
                establishGuestSession()
            }
            return
        }

        val user = try {
            val entity = firestoreDataSource.getUser(uid)
            entity?.toDomain()
                ?: if (bootstrap) {
                    val healed = UserEntity(id = uid, email = email, isGuest = false)
                    runCatching { firestoreDataSource.saveUser(healed) }
                    healed.toDomain()
                } else {
                    User(id = uid, email = email)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load user document", e)
            User(id = uid, email = email)
        }
        setState(AuthState.Authenticated(user.copy(isGuest = false)))
    }

    private suspend fun establishGuestSession() {
        val uid = try {
            authDataSource.signInAnonymously().user?.uid
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in failed", e)
            null
        }
        if (uid == null) {
            setState(AuthState.Guest(guestUser(id = "")))
            return
        }
        setState(AuthState.Guest(loadOrCreateGuestUser(uid)))
    }

    private suspend fun loadOrCreateGuestUser(uid: String): User {
        return try {
            val existing = firestoreDataSource.getUser(uid)
            if (existing != null) {
                existing.toDomain().copy(isGuest = true)
            } else {
                val entity = UserEntity(id = uid, firstName = "Guest", isGuest = true)
                runCatching { firestoreDataSource.saveUser(entity) }
                    .onFailure { Log.e(TAG, "Failed to create guest document", it) }
                entity.toDomain()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load guest document", e)
            guestUser(id = uid)
        }
    }

    private fun guestUser(id: String) = User(id = id, firstName = "Guest", isGuest = true)

    private suspend fun setState(state: AuthState) {
        _authState.value = state
        val user = when (state) {
            is AuthState.Guest -> state.user
            is AuthState.Authenticated -> state.user
            AuthState.Loading -> null
        }
        if (user != null) {
            runCatching { sessionPreferencesRepository.saveUserSession(user) }
                .onFailure { Log.e(TAG, "Failed to persist session", it) }
        }
    }

    private companion object {
        const val TAG = "AuthSessionManager"
    }
}
