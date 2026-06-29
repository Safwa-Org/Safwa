package com.tasneem.safwa.features.auth.presentation.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.usecase.RegisterUseCase
import com.tasneem.safwa.features.auth.domain.usecase.SendVerificationEmailUseCase
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
            is RegisterEvent.FirstNameChanged -> {
                _state.update { it.copy(firstName = event.firstName, firstNameErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
            }
            is RegisterEvent.LastNameChanged -> {
                _state.update { it.copy(lastName = event.lastName, lastNameErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
            }
            is RegisterEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, emailErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
            }
            is RegisterEvent.PhoneChanged -> {
                _state.update { it.copy(phone = event.phone, phoneErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, passwordErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword, confirmPasswordErrorResId = null, generalErrorMessage = null, generalErrorResId = null) }
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
        val firstName = currentState.firstName.trim()
        val lastName = currentState.lastName.trim()
        val email = currentState.email.trim()
        val phone = currentState.phone.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        _state.update {
            it.copy(
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

        if (firstName.isBlank()) {
            _state.update { it.copy(firstNameErrorResId = R.string.error_first_name_empty) }
            return
        }
        if (!firstName.matches(Regex("^[A-Za-z]+$"))) {
            _state.update { it.copy(firstNameErrorResId = R.string.error_name_letters_only) }
            return
        }
        if (lastName.isBlank()) {
            _state.update { it.copy(lastNameErrorResId = R.string.error_last_name_empty) }
            return
        }
        if (!lastName.matches(Regex("^[A-Za-z]+$"))) {
            _state.update { it.copy(lastNameErrorResId = R.string.error_name_letters_only) }
            return
        }
        if (email.isBlank()) {
            _state.update { it.copy(emailErrorResId = R.string.error_email_empty) }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            return
        }
        val phoneRegex = Regex("^(010|011|012|015)[0-9]{8}$")
        if (phone.isNotBlank() && !phone.matches(phoneRegex)) {
            _state.update { it.copy(phoneErrorResId = R.string.error_phone_egypt_invalid) }
            return
        }
        if (password.isBlank()) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_empty) }
            return
        }
        if (password.length < 6) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_too_short) }
            return
        }
        if (!password.any { it.isUpperCase() }) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_no_uppercase) }
            return
        }
        if (!password.any { it.isLowerCase() }) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_no_lowercase) }
            return
        }
        if (!password.any { it.isDigit() }) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_no_digit) }
            return
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_no_symbol) }
            return
        }
        if (confirmPassword.isBlank()) {
            _state.update { it.copy(confirmPasswordErrorResId = R.string.error_confirm_password_empty) }
            return
        }
        if (password != confirmPassword) {
            _state.update { it.copy(confirmPasswordErrorResId = R.string.error_passwords_do_not_match) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = registerUseCase(email, password, firstName, lastName, phone)
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
            is Resource.Loading -> { }
        }
    }
}