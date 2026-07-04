package com.tasneem.safwa.features.settings.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.auth.domain.usecase.LogoutUseCase
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEffect
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEvent
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val preferencesUseCases: PreferencesUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(ProfileEvent.LoadProfile)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> {
                _state.update { it.copy(isLoading = true) }

                // 1. Collect User Details
                viewModelScope.launch {
                    preferencesUseCases.getUserSession().collect { user ->
                        if (user != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    firstName = user.firstName,
                                    lastName = user.lastName,
                                    email = user.email ?: "",
                                    photoUrl = user.photoUrl,
                                    isElite = true,
                                    ordersCount = 12,
                                    wishlistCount = 8,
                                    savedAddressesCount = user.addresses.size,
                                    points = 2400
                                )
                            }
                        }
                    }
                }

                viewModelScope.launch {
                    preferencesUseCases.getAppPreferences().collect { prefs ->
                        _state.update {
                            it.copy(
                                // Transforming raw keys into beautiful user-facing strings
                                language = mapLanguageCodeToName(prefs.languageCode),
                                currency = mapCurrencyCodeToDisplay(prefs.currencyCode),
                                isDarkMode = prefs.isDarkMode
                            )
                        }
                    }
                }            }

            is ProfileEvent.OrderHistoryClicked -> {
                viewModelScope.launch {
                    _effect.send(ProfileEffect.NavigateToOrderHistory)
                }
            }

            is ProfileEvent.SavedAddressesClicked -> {
                viewModelScope.launch {
                    _effect.send(ProfileEffect.NavigateToSavedAddresses)
                }
            }

            is ProfileEvent.LanguageAndCurrencyClicked -> {
                viewModelScope.launch {
                    _effect.send(ProfileEffect.NavigateToLanguageAndCurrency)
                }
            }

            is ProfileEvent.DarkModeToggled -> {
                _state.update { it.copy(isDarkMode = event.isDarkMode) }
                viewModelScope.launch {
                    preferencesUseCases.updateAppPreferences.updateTheme(event.isDarkMode)
                }
            }

            is ProfileEvent.LogoutClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    logoutUseCase()
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(ProfileEffect.NavigateToLogin)
                }
            }
        }
    }


    private fun mapLanguageCodeToName(code: String?): String {
        return when (code?.lowercase()) {
            "ar" -> "العربية"
            "en" -> "English"
            else -> "English"
        }
    }

    private fun mapCurrencyCodeToDisplay(code: String?): String {
        return when (code?.uppercase()) {
            "EGP" -> "EGP (🇪🇬)"
            "USD" -> "USD (🇺🇸)"
            "EUR" -> "EUR (🇪🇺)"
            "GBP" -> "GBP (🇬🇧)"
            "SAR" -> "SAR (🇸🇦)"
            else -> code ?: "USD"
        }
    }
}