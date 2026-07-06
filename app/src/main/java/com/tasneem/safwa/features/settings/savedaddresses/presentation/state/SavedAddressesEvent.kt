package com.tasneem.safwa.features.settings.savedaddresses.presentation.state

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country

sealed interface SavedAddressesEvent {
    data object LoadAddresses : SavedAddressesEvent
    data object AddNewAddressClicked : SavedAddressesEvent
    data class EditAddressClicked(val address: Address) : SavedAddressesEvent
    data class DeleteAddressClicked(val address: Address) : SavedAddressesEvent

    data class CountrySelected(val country: Country) : SavedAddressesEvent
    data class AddressQueryChanged(val query: String) : SavedAddressesEvent
    data class SuggestionSelected(val candidate: AddressCandidate) : SavedAddressesEvent

    data class SaveAddress(
        val firstName: String,
        val lastName: String,
        val mobileNumber: String,
        val apartment: String,
        val setAsDefault: Boolean
    ) : SavedAddressesEvent

    data object ConfirmDeleteAddress : SavedAddressesEvent
    data object DismissDialogs : SavedAddressesEvent
    data object BackClicked : SavedAddressesEvent
}