package com.tasneem.safwa.features.settings.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // TODO: Inject UseCases here later (e.g., GetUserProfileUseCase, UpdateThemeUseCase, LogoutUseCase)
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

                // TODO: Replace with actual data fetching from UseCase later
                // Simulating a successful data load with dummy data
                _state.update {
                    it.copy(
                        isLoading = false,
                        firstName = "Osama",
                        lastName = "Khaled",
                        email = "osama@safwa.com",
                        photoUrl = null,
                        isElite = true,
                        ordersCount = 12,
                        wishlistCount = 8,
                        savedAddressesCount = 2,
                        points = 2400,
                        language = "English",
                        currency = "USD",
                        isDarkMode = false,
                        errorMessage = null
                    )
                }
            }

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
                // Update local UI state
                _state.update { it.copy(isDarkMode = event.isDarkMode) }

                // TODO: Launch coroutine to save this preference in DataStore/SharedPreferences via UseCase
            }

            is ProfileEvent.LogoutClicked -> {
                viewModelScope.launch {
                    // TODO: Call LogoutUseCase to clear tokens/session before navigating
                    _effect.send(ProfileEffect.NavigateToLogin)
                }
            }
        }
    }
}