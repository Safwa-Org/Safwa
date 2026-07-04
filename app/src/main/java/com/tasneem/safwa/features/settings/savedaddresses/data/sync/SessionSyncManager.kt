package com.tasneem.safwa.features.settings.savedaddresses.data.sync

import com.tasneem.safwa.core.di.ApplicationScope
import com.tasneem.safwa.core.domain.repository.RemoteUserRepository
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class SessionSyncManager @Inject constructor(
    private val localRepo: SessionPreferencesRepository,
    private val remoteRepo: RemoteUserRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    fun startRealTimeSync() {
        externalScope.launch {
            localRepo.isLoggedIn
                .distinctUntilChanged()
                .collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    val userId = localRepo.currentUser.first()?.id ?: return@collectLatest
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