package com.tasneem.safwa.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.tasneem.safwa.core.data.mapper.toDomain
import com.tasneem.safwa.core.data.mapper.toEntity
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.data.source.local.PreferencesKeys
import com.tasneem.safwa.core.domain.model.AppPreferences
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.domain.repository.SessionPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SessionPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SessionPreferencesRepository {

    override val currentUser: Flow<User?> = dataStore.data.map { preferences ->
        val userJson = preferences[PreferencesKeys.USER_SESSION_JSON]
        if (userJson != null) {
            try {
                Json.decodeFromString<UserEntity>(userJson).toDomain()
            } catch (e: Exception) { null }
        } else null
    }

    override val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    override suspend fun saveUserSession(user: User) {
        val userJson = Json.encodeToString(user.toEntity())
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_SESSION_JSON] = userJson
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_SESSION_JSON)
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }

    override val appPreferences: Flow<AppPreferences> = dataStore.data.map { preferences ->
        AppPreferences(
            isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false,
            languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: "en",
            currencyCode = preferences[PreferencesKeys.CURRENCY_CODE] ?: "USD",
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            isOnboardingCompleted = preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
        )
    }

    override suspend fun updateTheme(isDarkMode: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = isDarkMode }
    }

    override suspend fun updateLanguage(languageCode: String) {
        dataStore.edit { it[PreferencesKeys.LANGUAGE_CODE] = languageCode }
    }

    override suspend fun updateCurrency(currencyCode: String) {
        dataStore.edit { it[PreferencesKeys.CURRENCY_CODE] = currencyCode }
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_ONBOARDING_COMPLETED] = completed }
    }
}