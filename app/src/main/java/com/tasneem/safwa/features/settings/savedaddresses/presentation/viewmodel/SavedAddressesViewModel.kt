package com.tasneem.safwa.features.settings.savedaddresses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.DeleteAddressUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressSuggestionsUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetCountriesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetSavedAddressesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.UpdateAddressUseCase
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedAddressesViewModel @Inject constructor(
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getAddressSuggestionsUseCase: GetAddressSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SavedAddressesState())
    val state: StateFlow<SavedAddressesState> = _state.asStateFlow()

    private val _effect = Channel<SavedAddressesEffect>()
    val effect = _effect.receiveAsFlow()

    private val addressQueryFlow = MutableStateFlow("")


    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getSavedAddressesUseCase().collect { addresses ->
                _state.update { it.copy(isLoading = false, addresses = addresses) }
            }
        }
        viewModelScope.launch {
            getCountriesUseCase().onSuccess { list ->
                _state.update { it.copy(countries = list) }
            }
        }
        viewModelScope.launch {
            addressQueryFlow
                .debounce(350)
                .distinctUntilChanged()
                .filter { it.length >= 3 && _state.value.selectedCountry != null }
                .flatMapLatest { query ->
                    flow {
                        emit(getAddressSuggestionsUseCase(query, _state.value.selectedCountry!!.iso2))
                    }
                }
                .collect { result ->
                    result.onSuccess { suggestions ->
                        _state.update { it.copy(suggestions = suggestions, isSearchingAddress = false) }
                    }.onFailure {
                        _state.update { it.copy(suggestions = emptyList(), isSearchingAddress = false) }
                    }
                }
        }
    }

    fun onEvent(event: SavedAddressesEvent) {
        when (event) {
            is SavedAddressesEvent.LoadAddresses -> Unit

            is SavedAddressesEvent.AddNewAddressClicked -> {
                _state.update {
                    it.copy(
                        addressToEdit = null,
                        showEditDialog = true,
                        selectedCountry = null,
                        addressQuery = "",
                        suggestions = emptyList(),
                        selectedCandidate = null,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        addressErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.EditAddressClicked -> {
                val addr = event.address
                val country = _state.value.countries.firstOrNull { it.iso2 == addr.countryCode }
                val prefillCandidate = if (addr.isValidated && addr.latitude != null && addr.longitude != null) {
                    AddressCandidate(
                        id = "",
                        displayLabel = listOfNotNull(
                            addr.street,
                            addr.cityAndZip
                        ).joinToString(", "),
                        street = addr.street,
                        cityAndZip = addr.cityAndZip,
                        countryCode = addr.countryCode,
                        latitude = addr.latitude,
                        longitude = addr.longitude
                    )
                } else null

                _state.update {
                    it.copy(
                        addressToEdit = addr,
                        showEditDialog = true,
                        selectedCountry = country,
                        addressQuery = prefillCandidate?.displayLabel ?: "",
                        suggestions = emptyList(),
                        selectedCandidate = prefillCandidate,
                        recipientNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        addressErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.DeleteAddressClicked -> {
                _state.update { it.copy(addressToDelete = event.address) }
            }

            is SavedAddressesEvent.CountrySelected -> {
                _state.update {
                    it.copy(
                        selectedCountry = event.country,
                        suggestions = emptyList(),
                        addressQuery = "",
                        selectedCandidate = null
                    )
                }
            }

            is SavedAddressesEvent.AddressQueryChanged -> {
                _state.update { it.copy(addressQuery = event.query, isSearchingAddress = true, selectedCandidate = null) }
                addressQueryFlow.value = event.query
            }

            is SavedAddressesEvent.SuggestionSelected -> {
                _state.update {
                    it.copy(
                        selectedCandidate = event.candidate,
                        addressQuery = event.candidate.displayLabel,
                        suggestions = emptyList()
                    )
                }
            }

            is SavedAddressesEvent.SaveAddress -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            recipientNameErrorResId = null,
                            mobileNumberErrorResId = null,
                            addressErrorResId = null
                        )
                    }

                    val candidate = _state.value.selectedCandidate
                    val addressToSave = Address(
                        id = _state.value.addressToEdit?.id ?: "",
                        label = event.label.ifBlank { "Address" },
                        recipientName = event.recipientName,
                        mobileNumber = event.mobileNumber,
                        street = candidate?.street ?: "",
                        cityAndZip = candidate?.cityAndZip ?: "",
                        countryCode = candidate?.countryCode ?: "",
                        latitude = candidate?.latitude,
                        longitude = candidate?.longitude,
                        isValidated = candidate != null
                    )

                    val result = updateAddressUseCase(addressToSave)

                    result.onSuccess {
                        _state.update {
                            it.copy(
                                showEditDialog = false,
                                addressToEdit = null,
                                isLoading = false
                            )
                        }
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
                                        AddressFieldError.ADDRESS_NOT_SELECTED ->
                                            updatedState.copy(addressErrorResId = R.string.err_address_not_selected)
                                    }
                                }
                                updatedState
                            }
                        } else {
                            _state.update { it.copy(showEditDialog = false, addressToEdit = null) }
                            _effect.send(SavedAddressesEffect.ShowError(error.message ?: "An unknown error occurred."))
                        }
                    }
                }
            }

            is SavedAddressesEvent.ConfirmDeleteAddress -> {
                val targetId = _state.value.addressToDelete?.id
                if (targetId != null) {
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true) }
                        val result = deleteAddressUseCase(targetId)
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
                        addressErrorResId = null
                    )
                }
            }
            is SavedAddressesEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(SavedAddressesEffect.NavigateBack) }
            }        }
    }
}