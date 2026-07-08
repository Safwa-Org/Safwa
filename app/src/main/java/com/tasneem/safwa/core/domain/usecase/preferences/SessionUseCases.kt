package com.tasneem.safwa.core.domain.usecase.preferences

import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.apolloStore
import com.tasneem.safwa.core.domain.model.AppPreferences
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    operator fun invoke(): Flow<User?> = repo.currentUser
}
class SaveUserSessionUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    suspend operator fun invoke(user: User) = repo.saveUserSession(user)
}

class GetAppPreferencesUseCase @Inject constructor(private val repo: SessionPreferencesRepository) {
    operator fun invoke(): Flow<AppPreferences> = repo.appPreferences
}

class UpdateAppPreferencesUseCase @Inject constructor(
    private val repo: SessionPreferencesRepository,
    private val apolloClient: ApolloClient
) {
    suspend fun updateTheme(isDark: Boolean) = repo.updateTheme(isDark)
    suspend fun updateLanguage(lang: String) {
        repo.updateLanguage(lang)
        apolloClient.apolloStore.clearAll()
    }

    suspend fun updateCurrency(currency: String) {
        repo.updateCurrency(currency)
        apolloClient.apolloStore.clearAll()
    }

    suspend fun setOnboardingCompleted(completed: Boolean) = repo.setOnboardingCompleted(completed)
}


