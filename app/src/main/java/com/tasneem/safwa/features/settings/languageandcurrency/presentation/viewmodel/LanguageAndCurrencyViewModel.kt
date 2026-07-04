package com.tasneem.safwa.features.settings.languageandcurrency.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.settings.languageandcurrency.domain.usecase.GetSupportedCurrenciesUseCase
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
    private val preferencesUseCases: PreferencesUseCases,
    private val getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase
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

                    try {
                        val prefs = preferencesUseCases.getAppPreferences().first()
                        val currencies = getSupportedCurrenciesUseCase().first()

                        _state.update {
                            it.copy(
                                isLoading = false,
                                selectedLanguageCode = prefs.languageCode,
                                selectedCurrencyCode = prefs.currencyCode,
                                availableCurrencies = currencies
                            )
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        // Handle error - maybe show a snackbar or log it
                    }
                }
            }

            is LanguageAndCurrencyEvent.LanguageSelected -> {
                _state.update { it.copy(selectedLanguageCode = event.code) }
            }

            is LanguageAndCurrencyEvent.CurrencySelected -> {
                _state.update { it.copy(selectedCurrencyCode = event.code) }
            }

            is LanguageAndCurrencyEvent.SavePreferencesClicked -> {
                viewModelScope.launch {
                    preferencesUseCases.updateAppPreferences.updateLanguage(_state.value.selectedLanguageCode)
                    preferencesUseCases.updateAppPreferences.updateCurrency(_state.value.selectedCurrencyCode)

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