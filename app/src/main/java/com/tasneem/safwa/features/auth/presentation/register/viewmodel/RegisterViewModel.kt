package com.tasneem.safwa.features.auth.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.auth.domain.usecase.RegisterUseCase
import com.tasneem.safwa.features.auth.domain.usecase.SendVerificationEmailUseCase
import com.tasneem.safwa.features.auth.presentation.register.state.RegisterEvent
import com.tasneem.safwa.features.auth.presentation.register.state.RegisterSideEffect
import com.tasneem.safwa.features.auth.presentation.register.state.RegisterState
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _sideEffect = Channel<RegisterSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FormInputChanged -> {
                _state.update {
                    it.copy(
                        firstName = event.firstName ?: it.firstName,
                        lastName = event.lastName ?: it.lastName,
                        email = event.email ?: it.email,
                        phone = event.phone ?: it.phone,
                        password = event.password ?: it.password,
                        confirmPassword = event.confirmPassword ?: it.confirmPassword,
                        firstNameErrorResId = null,
                        lastNameErrorResId = null,
                        emailErrorResId = null,
                        phoneErrorResId = null,
                        passwordErrorResId = null,
                        confirmPasswordErrorResId = null,
                        generalErrorMessage = null,
                        generalErrorResId = null
                    )
                }
            }
            RegisterEvent.RegisterClicked -> executeRegistration()
            RegisterEvent.LoginClicked -> {
                viewModelScope.launch { _sideEffect.send(RegisterSideEffect.NavigateToLogin) }
            }
            RegisterEvent.ResendVerification -> {
                viewModelScope.launch {
                    sendVerificationEmailUseCase()
                    _sideEffect.send(RegisterSideEffect.ShowToast(message = "Verification email resent"))
                }
            }
        }
    }

    private fun executeRegistration() {
        val currentState = _state.value

        _state.update {
            it.copy(
                firstNameErrorResId = null,
                lastNameErrorResId = null,
                emailErrorResId = null,
                phoneErrorResId = null,
                passwordErrorResId = null,
                confirmPasswordErrorResId = null,
                generalErrorMessage = null,
                generalErrorResId = null,
                isLoading = true
            )
        }

        viewModelScope.launch {
            val result = registerUseCase(
                email = currentState.email,
                password = currentState.password,
                confirmPassword = currentState.confirmPassword,
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                phone = currentState.phone
            )
            handleRegistrationResult(result)
        }
    }
    private suspend fun handleRegistrationResult(result: Resource<User>) {
        _state.update { it.copy(isLoading = false) }
        when (result) {
            is Resource.Success -> {
                _state.update { it.copy(showVerificationDialog = true) }
            }
            is Resource.Error -> {
                val message = result.message ?: "Registration failed"
                when (message) {
                    // Use case validation mappings
                    "error_first_name_empty" -> _state.update { it.copy(firstNameErrorResId = R.string.error_first_name_empty) }
                    "error_name_letters_only_first" -> _state.update { it.copy(firstNameErrorResId = R.string.error_name_letters_only) }
                    "error_last_name_empty" -> _state.update { it.copy(lastNameErrorResId = R.string.error_last_name_empty) }
                    "error_name_letters_only_last" -> _state.update { it.copy(lastNameErrorResId = R.string.error_name_letters_only) }
                    "error_email_empty" -> _state.update { it.copy(emailErrorResId = R.string.error_email_empty) }
                    "error_invalid_email" -> _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
                    "error_phone_egypt_invalid" -> _state.update { it.copy(phoneErrorResId = R.string.error_phone_egypt_invalid) }
                    "error_password_empty" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_empty) }
                    "error_password_too_short" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_too_short) }
                    "error_password_no_uppercase" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_no_uppercase) }
                    "error_password_no_lowercase" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_no_lowercase) }
                    "error_password_no_digit" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_no_digit) }
                    "error_password_no_symbol" -> _state.update { it.copy(passwordErrorResId = R.string.error_password_no_symbol) }
                    "error_confirm_password_empty" -> _state.update { it.copy(confirmPasswordErrorResId = R.string.error_confirm_password_empty) }
                    "error_passwords_do_not_match" -> _state.update { it.copy(confirmPasswordErrorResId = R.string.error_passwords_do_not_match) }
                    else -> {
                        when {
                            message.contains("email already in use", ignoreCase = true) -> {
                                _state.update { it.copy(emailErrorResId = R.string.error_email_exists) }
                            }
                            message.contains("password too weak", ignoreCase = true) -> {
                                _state.update { it.copy(passwordErrorResId = R.string.error_password_weak) }
                            }
                            message.contains("invalid email", ignoreCase = true) -> {
                                _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
                            }
                            else -> {
                                _state.update {
                                    it.copy(
                                        generalErrorMessage = message,
                                        generalErrorResId = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is Resource.Loading -> { }
        }
    }}