package com.tasneem.safwa.features.settings.savedaddresses.presentation.state

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country

data class SavedAddressesState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val addressToEdit: Address? = null,
    val addressToDelete: Address? = null,
    val showEditDialog: Boolean = false,

    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,

    val addressQuery: String = "",
    val suggestions: List<AddressCandidate> = emptyList(),
    val isSearchingAddress: Boolean = false,
    val selectedCandidate: AddressCandidate? = null,
    val isOffline: Boolean = false,

    val firstNameErrorResId: Int? = null,
    val lastNameErrorResId: Int? = null,
    val mobileNumberErrorResId: Int? = null,
    val addressErrorResId: Int? = null,

    val isShowingCachedAddresses: Boolean = false
)