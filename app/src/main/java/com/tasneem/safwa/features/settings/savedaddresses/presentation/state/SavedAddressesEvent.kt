package com.tasneem.safwa.features.settings.savedaddresses.presentation.state


import com.tasneem.safwa.core.domain.model.Address

sealed interface SavedAddressesEvent {
    data object LoadAddresses : SavedAddressesEvent
    data object AddNewAddressClicked : SavedAddressesEvent
    data class EditAddressClicked(val address: Address) : SavedAddressesEvent
    data class DeleteAddressClicked(val address: Address) : SavedAddressesEvent
    data class SaveAddress(val address: Address) : SavedAddressesEvent
    data object ConfirmDeleteAddress : SavedAddressesEvent
    data object DismissDialogs : SavedAddressesEvent
    data object BackClicked : SavedAddressesEvent
}