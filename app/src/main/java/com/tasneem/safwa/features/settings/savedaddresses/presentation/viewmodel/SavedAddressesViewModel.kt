package com.tasneem.safwa.features.settings.savedaddresses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEffect
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEvent
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesState
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressFieldError
import com.tasneem.safwa.features.settings.savedaddresses.util.AddressValidationException
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
class SavedAddressesViewModel @Inject constructor(
    private val preferencesUseCases: PreferencesUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SavedAddressesState())
    val state: StateFlow<SavedAddressesState> = _state.asStateFlow()

    private val _effect = Channel<SavedAddressesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            preferencesUseCases.getSavedAddresses().collect { addresses ->
                _state.update { it.copy(isLoading = false, addresses = addresses) }
            }
        }
    }

    fun onEvent(event: SavedAddressesEvent) {
        when (event) {
            is SavedAddressesEvent.LoadAddresses -> {
            }

            is SavedAddressesEvent.AddNewAddressClicked -> {
                _state.update {
                    it.copy(
                        addressToEdit = null,
                        showEditDialog = true,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        streetErrorResId = null,
                        cityZipErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.EditAddressClicked -> {
                _state.update {
                    it.copy(
                        addressToEdit = event.address,
                        showEditDialog = true,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        streetErrorResId = null,
                        cityZipErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.DeleteAddressClicked -> {
                _state.update { it.copy(addressToDelete = event.address) }
            }

            is SavedAddressesEvent.SaveAddress -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isLoading = true,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        streetErrorResId = null,
                        cityZipErrorResId = null
                    )}

                    val result = preferencesUseCases.updateAddress(event.address)

                    result.onSuccess {
                        _state.update { it.copy(
                            showEditDialog = false,
                            addressToEdit = null,
                            isLoading = false
                        )}
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false) }

                        if (error is AddressValidationException) {
                            _state.update { currentState ->
                                var updatedState = currentState
                                error.errors.forEach { fieldError ->
                                    updatedState = when (fieldError) {
                                        AddressFieldError.RECIPIENT_NAME_EMPTY ->
                                            updatedState.copy(recipientNameErrorResId = R.string.err_empty_name)
                                        AddressFieldError.MOBILE_NUMBER_INVALID ->
                                            updatedState.copy(mobileNumberErrorResId = R.string.err_invalid_mobile)
                                        AddressFieldError.STREET_EMPTY ->
                                            updatedState.copy(streetErrorResId = R.string.err_empty_street)
                                    }
                                }
                                updatedState
                            }
                        } else {
                            _state.update { it.copy(
                                showEditDialog = false,
                                addressToEdit = null
                            )}

                            _effect.send(SavedAddressesEffect.ShowError(error.message ?: "An unknown error occurred."))                        }
                    }
                }
            }

            is SavedAddressesEvent.ConfirmDeleteAddress -> {
                val targetId = _state.value.addressToDelete?.id
                if (targetId != null) {
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true) }

                        val result = preferencesUseCases.deleteAddress(targetId)

                        _state.update { it.copy(isLoading = false) }

                        result.onFailure { error ->
                            _effect.send(
                                SavedAddressesEffect.ShowError(
                                    error.message ?: "Could not remove address from server."
                                )
                            )
                        }
                    }
                }
                _state.update { it.copy(addressToDelete = null) }
            }
            is SavedAddressesEvent.DismissDialogs -> {
                _state.update {
                    it.copy(
                        showEditDialog = false,
                        addressToEdit = null,
                        addressToDelete = null,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        streetErrorResId = null,
                        cityZipErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(SavedAddressesEffect.NavigateBack) }
            }
        }
    }
}