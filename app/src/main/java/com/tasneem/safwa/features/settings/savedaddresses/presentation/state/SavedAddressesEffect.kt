package com.tasneem.safwa.features.settings.savedaddresses.presentation.state

sealed interface SavedAddressesEffect {
    data object NavigateBack : SavedAddressesEffect
    data class ShowError(val messageResId: Int) : SavedAddressesEffect
}