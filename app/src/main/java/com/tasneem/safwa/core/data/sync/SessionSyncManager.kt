package com.tasneem.safwa.core.data.sync

import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Singleton
class SessionSyncManager @Inject constructor(
    private val localRepo: SessionPreferencesRepository,
    private val remoteRepo: RemoteUserRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    fun startRealTimeSync() {
        externalScope.launch {
            // Keyed on the session's user id (not a logged-in flag): with
            // anonymous auth there is always a session, and the id changes on
            // every guest→account or logout→guest transition.
            localRepo.currentUser
                .map { it?.id?.takeIf { id -> id.isNotEmpty() } }
                .distinctUntilChanged()
                .collectLatest { userId ->
                    if (userId != null) {
                        remoteRepo.observeUser(userId).collect { remoteUser ->
                            if (remoteUser != null) {
                                localRepo.saveUserSession(remoteUser)
                            }
                        }
                    }
                }
        }
    }
}
