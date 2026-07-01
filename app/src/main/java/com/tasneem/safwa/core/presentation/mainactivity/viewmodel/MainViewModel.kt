package com.tasneem.safwa.core.presentation.mainactivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.core.navigation.StartDestination
import com.tasneem.safwa.features.auth.domain.usecase.ObserveAuthStateUseCase
import com.tasneem.safwa.features.auth.domain.usecase.SyncUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesUseCases: PreferencesUseCases,
    syncUserSessionUseCase: SyncUserSessionUseCase,
    observeAuthStateUseCase: ObserveAuthStateUseCase,
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferencesUseCases.getAppPreferences()
        .map { it.isDarkMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val authState: StateFlow<AuthState> = observeAuthStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Loading,
        )

    val startDestination: StateFlow<StartDestination> = combine(
        observeAuthStateUseCase(),
        preferencesUseCases.getAppPreferences(),
    ) { auth, prefs ->
        when (auth) {
            AuthState.Loading -> StartDestination.Loading
            else -> if (prefs.isOnboardingCompleted) StartDestination.Home else StartDestination.Onboarding
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = StartDestination.Loading,
    )

    fun markOnboardingCompleted() {
        viewModelScope.launch {
            preferencesUseCases.updateAppPreferences.setOnboardingCompleted(true)
        }
    }

    init {
        viewModelScope.launch {
            syncUserSessionUseCase()
        }
    }
}