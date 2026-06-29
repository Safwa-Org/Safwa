package com.tasneem.safwa.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.tasneem.safwa.R

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        emailErrorResId = null,
                        generalErrorMessage = null,
                        generalErrorResId = null
                    )
                }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        passwordErrorResId = null,
                        generalErrorMessage = null,
                        generalErrorResId = null
                    )
                }
            }
            LoginEvent.LoginClicked -> {
                executeLogin()
            }
            LoginEvent.GoogleLoginClicked -> {
                // TODO: implement Google login
            }
            LoginEvent.SignUpClicked -> {
                viewModelScope.launch { _sideEffect.emit(LoginSideEffect.NavigateToSignUp) }
            }
            LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _sideEffect.emit(LoginSideEffect.NavigateToForgotPassword) }
            }
            LoginEvent.GuestClicked -> {
                viewModelScope.launch { _sideEffect.emit(LoginSideEffect.NavigateAsGuest) }
            }
        }
    }

    private fun executeLogin() {
        val currentState = _state.value
        val email = currentState.email.trim()
        val password = currentState.password

        _state.update {
            it.copy(
                emailErrorResId = null,
                passwordErrorResId = null,
                generalErrorMessage = null,
                generalErrorResId = null
            )
        }

        if (email.isBlank()) {
            _state.update { it.copy(emailErrorResId = R.string.error_email_empty) }
            return
        }

        if (password.isBlank()) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_empty) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // TODO: replace with real API call
                _sideEffect.emit(LoginSideEffect.NavigateToHome)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalErrorMessage = e.localizedMessage
                    )
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}