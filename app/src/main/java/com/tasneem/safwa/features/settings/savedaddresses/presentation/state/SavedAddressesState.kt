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
    val errorMessage: String? = null,

    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,

    val addressQuery: String = "",
    val suggestions: List<AddressCandidate> = emptyList(),
    val isSearchingAddress: Boolean = false,
    val selectedCandidate: AddressCandidate? = null,

    val recipientNameErrorResId: Int? = null,
    val mobileNumberErrorResId: Int? = null,
    val addressErrorResId: Int? = null
)