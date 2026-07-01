package com.tasneem.safwa.core.presentation.mainactivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.auth.domain.usecase.SyncUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesUseCases: PreferencesUseCases,
    syncUserSessionUseCase: SyncUserSessionUseCase
) : ViewModel() {

    val languageCode: StateFlow<String> = preferencesUseCases.getAppPreferences()
        .map { it.languageCode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "en"
        )
    val isDarkMode: StateFlow<Boolean> = preferencesUseCases.getAppPreferences()
        .map { it.isDarkMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            syncUserSessionUseCase()
        }
    }
}