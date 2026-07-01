package com.tasneem.safwa.core.domain.repository

import com.tasneem.safwa.core.domain.model.AppPreferences
import com.tasneem.safwa.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SessionPreferencesRepository {
    // User Session
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    suspend fun saveUserSession(user: User)
    suspend fun clearSession()

    // App Preferences
    val appPreferences: Flow<AppPreferences>
    suspend fun updateTheme(isDarkMode: Boolean)
    suspend fun updateLanguage(languageCode: String)
    suspend fun updateCurrency(currencyCode: String)
    suspend fun updateNotifications(enabled: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
}