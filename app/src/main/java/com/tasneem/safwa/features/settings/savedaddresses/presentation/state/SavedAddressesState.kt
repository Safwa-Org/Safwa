package com.tasneem.safwa.features.settings.savedaddresses.presentation.state

import com.tasneem.safwa.core.domain.model.Address

data class SavedAddressesState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val addressToEdit: Address? = null,
    val addressToDelete: Address? = null,
    val showEditDialog: Boolean = false,
    val errorMessage: String? = null,

    val recipientNameErrorResId: Int? = null,
    val mobileNumberErrorResId: Int? = null,
    val streetErrorResId: Int? = null,
    val cityZipErrorResId: Int? = null
)

