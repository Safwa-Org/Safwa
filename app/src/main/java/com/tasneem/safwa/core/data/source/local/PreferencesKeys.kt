package com.tasneem.safwa.core.data.source.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USER_SESSION_JSON = stringPreferencesKey("user_session_json")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val CURRENCY_CODE = stringPreferencesKey("currency_code")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")

    val RATES_JSON = stringPreferencesKey("cached_exchange_rates_json")
    val RATES_TIMESTAMP = longPreferencesKey("cached_exchange_rates_timestamp")
}