package com.tasneem.safwa.features.settings.languageandcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyEffect
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyEvent
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageAndCurrencyViewModel @Inject constructor(
    private val preferencesUseCases: PreferencesUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageAndCurrencyState())
    val state: StateFlow<LanguageAndCurrencyState> = _state.asStateFlow()

    private val _effect = Channel<LanguageAndCurrencyEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(LanguageAndCurrencyEvent.LoadPreferences)
    }

    fun onEvent(event: LanguageAndCurrencyEvent) {
        when (event) {
            is LanguageAndCurrencyEvent.LoadPreferences -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    // Load initial saved preferences
                    val prefs = preferencesUseCases.getAppPreferences().first()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedLanguageCode = prefs.languageCode,
                            // selectedCurrencyCode remains USD for now as requested
                        )
                    }
                }
            }

            is LanguageAndCurrencyEvent.LanguageSelected -> {
                _state.update { it.copy(selectedLanguageCode = event.code) }
            }

            is LanguageAndCurrencyEvent.CurrencySelected -> {
                // Currency logic is static for now, but we prepare the state
                _state.update { it.copy(selectedCurrencyCode = event.code) }
            }

            is LanguageAndCurrencyEvent.SavePreferencesClicked -> {
                viewModelScope.launch {
                    // Save the selected language to DataStore
                    preferencesUseCases.updateAppPreferences.updateLanguage(_state.value.selectedLanguageCode)

                    // Navigate back after saving
                    _effect.send(LanguageAndCurrencyEffect.NavigateBack)
                }
            }

            is LanguageAndCurrencyEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(LanguageAndCurrencyEffect.NavigateBack)
                }
            }
        }
    }
}