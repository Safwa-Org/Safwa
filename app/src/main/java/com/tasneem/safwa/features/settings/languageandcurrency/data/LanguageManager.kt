package com.tasneem.safwa.features.settings.languageandcurrency.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tasneem.safwa.core.data.source.local.PreferencesKeys
import com.tasneem.safwa.core.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val appScope: CoroutineScope
) {
    companion object {
        const val DEFAULT_LANGUAGE = "en"
    }

    private val _displayLanguage = MutableStateFlow(DEFAULT_LANGUAGE)
    val displayLanguage: StateFlow<String> = _displayLanguage.asStateFlow()

    init {
        appScope.launch {
            dataStore.data
                .map { it[PreferencesKeys.LANGUAGE_CODE] ?: DEFAULT_LANGUAGE }
                .distinctUntilChanged()
                .collect { _displayLanguage.value = it }
        }
    }
}
