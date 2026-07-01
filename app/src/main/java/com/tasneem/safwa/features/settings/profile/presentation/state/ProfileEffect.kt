package com.tasneem.safwa.features.settings.profile.presentation.state

sealed interface ProfileEffect {
    data object NavigateToOrderHistory : ProfileEffect
    data object NavigateToSavedAddresses : ProfileEffect
    data object NavigateToLanguageAndCurrency : ProfileEffect
    data object NavigateToLogin : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
}