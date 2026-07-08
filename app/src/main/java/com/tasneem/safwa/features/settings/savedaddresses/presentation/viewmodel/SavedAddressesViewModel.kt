package com.tasneem.safwa.features.settings.savedaddresses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.domain.usecase.preferences.PreferencesUseCases
import com.tasneem.safwa.core.util.NetworkStatusProvider
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.DeleteAddressUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressSuggestionsUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetAddressesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.GetCountriesUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.SaveAddressUseCase
import com.tasneem.safwa.features.settings.savedaddresses.domain.usecase.SetDefaultAddressUseCase
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEffect
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEvent
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SavedAddressesViewModel @Inject constructor(
    private val preferencesUseCases: PreferencesUseCases,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getAddressSuggestionsUseCase: GetAddressSuggestionsUseCase,
    private val networkStatusProvider: NetworkStatusProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SavedAddressesState())
    val state: StateFlow<SavedAddressesState> = _state.asStateFlow()

    private val _effect = Channel<SavedAddressesEffect>()
    val effect = _effect.receiveAsFlow()

    private val addressQueryFlow = MutableStateFlow("")

    init {
        loadAddresses()

        viewModelScope.launch {
            getCountriesUseCase().onSuccess { list ->
                _state.update { it.copy(countries = list) }
            }
        }

        viewModelScope.launch {
            addressQueryFlow
                .debounce(350.milliseconds)
                .distinctUntilChanged()
                .filter { it.length >= 3 && _state.value.selectedCountry != null }
                .flatMapLatest { query ->
                    flow { emit(getAddressSuggestionsUseCase(query, _state.value.selectedCountry!!.iso2)) }
                }
                .collect { result ->
                    result.onSuccess { suggestions ->
                        _state.update {
                            it.copy(suggestions = suggestions, isSearchingAddress = false, isOffline = false, addressErrorResId = null)
                        }
                    }.onFailure { error ->
                        val offline = error is IOException
                        _state.update {
                            it.copy(
                                suggestions = emptyList(),
                                isSearchingAddress = false,
                                isOffline = offline,
                                addressErrorResId = if (offline) R.string.err_no_internet else null
                            )
                        }
                    }
                }
        }
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val token = preferencesUseCases.getUserSession().first()?.customerAccessToken

            if (token.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, addresses = emptyList()) }
                _effect.send(SavedAddressesEffect.ShowError(messageResId = R.string.err_sign_in_required))
                return@launch
            }

            val isOffline = !networkStatusProvider.isConnected()

            getAddressesUseCase(token)
                .onSuccess { list ->
                    _state.update {
                        it.copy(isLoading = false, addresses = list, isShowingCachedAddresses = isOffline && list.isNotEmpty())
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(
                        SavedAddressesEffect.ShowError(
                            messageResId = if (isOffline) R.string.err_offline_no_cache else R.string.err_load_addresses_failed
                        )
                    )
                }
        }
    }
    fun onEvent(event: SavedAddressesEvent) {
        when (event) {
            is SavedAddressesEvent.LoadAddresses -> loadAddresses()

            is SavedAddressesEvent.AddNewAddressClicked -> {
                _state.update {
                    it.copy(
                        addressToEdit = null,
                        showEditDialog = true,
                        selectedCountry = null,
                        addressQuery = "",
                        suggestions = emptyList(),
                        selectedCandidate = null,
                        isOffline = false,
                        firstNameErrorResId = null,
                        lastNameErrorResId = null,
                        mobileNumberErrorResId = null,
                        addressErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.EditAddressClicked -> {
                val addr = event.address
                val country = _state.value.countries.firstOrNull { it.iso2 == addr.countryCode }
                val prefillCandidate = AddressCandidate(
                    id = addr.id,
                    displayLabel = listOfNotNull(addr.street, addr.city).joinToString(", "),
                    street = addr.street,
                    city = addr.city,
                    zip = addr.zip,
                    countryCode = addr.countryCode,
                    latitude = 0.0,
                    longitude = 0.0
                )
                _state.update {
                    it.copy(
                        addressToEdit = addr,
                        showEditDialog = true,
                        selectedCountry = country,
                        addressQuery = prefillCandidate.displayLabel,
                        suggestions = emptyList(),
                        selectedCandidate = prefillCandidate,
                        isOffline = false,
                        firstNameErrorResId = null,
                        lastNameErrorResId = null,
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
                    it.copy(selectedCountry = event.country, suggestions = emptyList(), addressQuery = "", selectedCandidate = null)
                }
            }

            is SavedAddressesEvent.AddressQueryChanged -> {
                if (!networkStatusProvider.isConnected()) {
                    _state.update {
                        it.copy(
                            addressQuery = event.query, isOffline = true, isSearchingAddress = false,
                            suggestions = emptyList(), selectedCandidate = null, addressErrorResId = R.string.err_no_internet
                        )
                    }
                    return
                }
                _state.update {
                    it.copy(addressQuery = event.query, isSearchingAddress = true, isOffline = false, addressErrorResId = null, selectedCandidate = null)
                }
                addressQueryFlow.value = event.query
            }

            is SavedAddressesEvent.SuggestionSelected -> {
                _state.update {
                    it.copy(selectedCandidate = event.candidate, addressQuery = event.candidate.displayLabel, suggestions = emptyList(), addressErrorResId = null)
                }
            }

            is SavedAddressesEvent.SaveAddress -> {
                val candidate = _state.value.selectedCandidate
                if (candidate == null) {
                    val offline = _state.value.isOffline
                    _state.update { it.copy(addressErrorResId = if (offline) R.string.err_no_internet else R.string.err_address_not_selected) }
                    viewModelScope.launch {
                        _effect.send(SavedAddressesEffect.ShowError(
                            messageResId = if (offline) R.string.msg_offline_search_address else R.string.msg_select_address_from_suggestions
                        ))
                    }
                    return
                }

                val phoneRegex = Regex("^(010|011|012|015)[0-9]{8}$")
                var hasFieldError = false
                if (event.firstName.isBlank()) {
                    _state.update { it.copy(firstNameErrorResId = R.string.err_first_name_empty) }
                    hasFieldError = true
                }
                if (!phoneRegex.matches(event.mobileNumber)) {
                    _state.update { it.copy(mobileNumberErrorResId = R.string.err_invalid_mobile) }
                    hasFieldError = true
                }
                if (hasFieldError) return

                viewModelScope.launch {
                    val token = preferencesUseCases.getUserSession().first()?.customerAccessToken
                    if (token.isNullOrEmpty()) {
                        _effect.send(SavedAddressesEffect.ShowError(messageResId = R.string.err_sign_in_required))
                        return@launch
                    }

                    _state.update { it.copy(isLoading = true, firstNameErrorResId = null, mobileNumberErrorResId = null) }

                    val addressToSave = Address(
                        id = _state.value.addressToEdit?.id ?: "",
                        firstName = event.firstName,
                        lastName = event.lastName,
                        phone = event.mobileNumber,
                        street = candidate.street ?: "",
                        apartment = event.apartment,
                        city = candidate.city,
                        zip = candidate.zip,
                        countryName = _state.value.selectedCountry?.name ?: "",
                        countryCode = candidate.countryCode,
                        isDefault = event.setAsDefault
                    )

                    saveAddressUseCase(token, addressToSave)
                        .onSuccess {
                            _state.update { it.copy(showEditDialog = false, addressToEdit = null, isLoading = false) }
                            loadAddresses()
                        }
                        .onFailure {
                            _state.update { it.copy(isLoading = false) }
                            _effect.send(SavedAddressesEffect.ShowError(messageResId = R.string.msg_unknown_error))
                        }
                }
            }
            is SavedAddressesEvent.ConfirmDeleteAddress -> {
                val target = _state.value.addressToDelete
                if (target != null) {
                    viewModelScope.launch {
                        val token = preferencesUseCases.getUserSession().first()?.customerAccessToken
                        if (token.isNullOrEmpty()) {
                            _effect.send(SavedAddressesEffect.ShowError(messageResId = R.string.err_sign_in_required))
                        } else {
                            _state.update { it.copy(isLoading = true) }
                            deleteAddressUseCase(token, target.id)
                                .onSuccess { loadAddresses() }
                                .onFailure {
                                    _state.update { it.copy(isLoading = false) }
                                    _effect.send(SavedAddressesEffect.ShowError(messageResId = R.string.msg_could_not_remove_address))
                                }
                        }
                    }
                }
                _state.update { it.copy(addressToDelete = null) }
            }

            is SavedAddressesEvent.DismissDialogs -> {
                _state.update {
                    it.copy(
                        showEditDialog = false, addressToEdit = null, addressToDelete = null,
                        firstNameErrorResId = null, lastNameErrorResId = null, mobileNumberErrorResId = null, addressErrorResId = null
                    )
                }
            }

            is SavedAddressesEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(SavedAddressesEffect.NavigateBack) }
            }
        }
    }
}