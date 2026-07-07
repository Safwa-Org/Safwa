package com.tasneem.safwa.features.settings.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.auth.domain.usecase.LogoutUseCase
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEffect
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEvent
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileState
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.settings.orderhistory.domain.usecase.GetOrdersUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val preferencesUseCases: PreferencesUseCases,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getAddressesUseCase: GetAddressesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    private val refreshTrigger = MutableStateFlow(0L)

    init {
        onEvent(ProfileEvent.LoadProfile)
        observeOrders()
        observeSavedAddressesCount()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeOrders() {
        viewModelScope.launch {
            preferencesUseCases.getUserSession()
                .map { it?.customerAccessToken }
                .distinctUntilChanged()
                .combine(refreshTrigger) { token, _ -> token }
                .flatMapLatest { token ->
                    if (!token.isNullOrEmpty()) {
                        getOrdersUseCase(token)
                    } else {
                        flowOf(Resource.Success(emptyList()))
                    }
                }
                .collect { result ->
                    if (result is Resource.Success) {
                        _state.update { it.copy(ordersCount = result.data.size) }
                    }
                }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeSavedAddressesCount() {
        viewModelScope.launch {
            preferencesUseCases.getUserSession()
                .map { it?.customerAccessToken }
                .distinctUntilChanged()
                .combine(refreshTrigger) { token, _ -> token }
                .flatMapLatest { token ->
                    if (!token.isNullOrEmpty()) {
                        flow { emit(getAddressesUseCase(token)) }
                    } else {
                        flowOf(Result.success(emptyList()))
                    }
                }
                .collect { result ->
                    result.onSuccess { addresses ->
                        _state.update { it.copy(savedAddressesCount = addresses.size) }
                    }
                }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Refresh -> {
                refreshTrigger.value = System.currentTimeMillis()
            }

            is ProfileEvent.LoadProfile -> {
                _state.update { it.copy(isLoading = true) }

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
                                language = mapLanguageCodeToName(prefs.languageCode),
                                currency = mapCurrencyCodeToDisplay(prefs.currencyCode),
                                isDarkMode = prefs.isDarkMode
                            )
                        }
                    }
                }
            }

            is ProfileEvent.OrderHistoryClicked -> {
                viewModelScope.launch { _effect.send(ProfileEffect.NavigateToOrderHistory) }
            }

            is ProfileEvent.SavedAddressesClicked -> {
                viewModelScope.launch { _effect.send(ProfileEffect.NavigateToSavedAddresses) }
            }

            is ProfileEvent.LanguageAndCurrencyClicked -> {
                viewModelScope.launch { _effect.send(ProfileEffect.NavigateToLanguageAndCurrency) }
            }

            is ProfileEvent.DarkModeToggled -> {
                _state.update { it.copy(isDarkMode = event.isDarkMode) }
                viewModelScope.launch {
                    preferencesUseCases.updateAppPreferences.updateTheme(event.isDarkMode)
                }
            }

            is ProfileEvent.LogoutClicked -> {
                _state.update { it.copy(showLogoutConfirmDialog = true) }
            }

            is ProfileEvent.ConfirmLogout -> {
                _state.update { it.copy(showLogoutConfirmDialog = false) }
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    logoutUseCase()
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(ProfileEffect.NavigateToLogin)
                }
            }

            is ProfileEvent.DismissLogoutDialog -> {
                _state.update { it.copy(showLogoutConfirmDialog = false) }
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