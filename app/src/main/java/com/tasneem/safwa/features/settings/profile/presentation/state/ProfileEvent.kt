package com.tasneem.safwa.features.settings.profile.presentation.state

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object OrderHistoryClicked : ProfileEvent
    data object SavedAddressesClicked : ProfileEvent
    data object LanguageAndCurrencyClicked : ProfileEvent
    data class DarkModeToggled(val isDarkMode: Boolean) : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object ConfirmLogout : ProfileEvent
    data object DismissLogoutDialog : ProfileEvent
    data object SignInClicked : ProfileEvent
    data object CreateAccountClicked : ProfileEvent
}