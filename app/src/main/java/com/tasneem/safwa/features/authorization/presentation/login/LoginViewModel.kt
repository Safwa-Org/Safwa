package com.tasneem.safwa.features.authorization.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.tasneem.safwa.R


class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    private val _sideEffect = Channel<LoginSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, errorMessage = null, errorResId = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, errorMessage = null, errorResId = null) }
            }
            LoginEvent.LoginClicked -> {
                executeLogin()
            }
            LoginEvent.GoogleLoginClicked -> {
                // Implement Google Auth similarly
            }
            LoginEvent.SignUpClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToSignUp) }
            }
            LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToForgotPassword) }
            }
            LoginEvent.GuestClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateAsGuest) }
            }
        }
    }

    private fun executeLogin() {
        val currentEmail = _state.value.email
        val currentPassword = _state.value.password

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _state.update { it.copy(errorResId = R.string.error_fields_empty) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, errorResId = null) }

            try {
                _sideEffect.send(LoginSideEffect.NavigateToHome)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}