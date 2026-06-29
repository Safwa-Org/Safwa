package com.tasneem.safwa.features.auth.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GoogleLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GuestLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val guestLoginUseCase: GuestLoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result is Resource.Success && result.data != null) {
                val user = result.data
                _state.update { it.copy(isGuest = user.isGuest) }
                // Navigate to home (both guest and normal users go to the same home)
                _sideEffect.emit(LoginSideEffect.NavigateToHome)
            }
        }
    }

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
                // The UI will handle Google Sign-In and then call handleGoogleLogin(idToken)
                // We just show a toast to indicate it's starting
            }
            LoginEvent.SignUpClicked -> {
                viewModelScope.launch { _sideEffect.emit(LoginSideEffect.NavigateToSignUp) }
            }
            LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _sideEffect.emit(LoginSideEffect.NavigateToForgotPassword) }
            }
            LoginEvent.GuestClicked -> {
                executeGuestLogin()
            }
            else -> {}
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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
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

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = loginUseCase(email, password)
            handleAuthResult(result)
        }
    }

    /**
     * Called from the UI after a successful Google Sign-In.
     * @param idToken The ID token from Google.
     */
    fun handleGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = googleLoginUseCase(idToken)
            handleAuthResult(result)
        }
    }

    private fun executeGuestLogin() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = guestLoginUseCase()
            if (result is Resource.Success) {
                _state.update { it.copy(isGuest = true, isLoading = false) }
                _sideEffect.emit(LoginSideEffect.NavigateToHome)
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalErrorResId = R.string.failed_guest
                    )
                }
            }
        }
    }

    private suspend fun handleAuthResult(result: Resource<User>) {
        _state.update { it.copy(isLoading = false) }
        when (result) {
            is Resource.Success -> {
                val user = result.data
                _state.update { it.copy(isGuest = user.isGuest) }
                _sideEffect.emit(LoginSideEffect.NavigateToHome)
            }
            is Resource.Error -> {
                val message = result.message
                when {
                    message?.contains("email", ignoreCase = true) == true ||
                            message?.contains("password", ignoreCase = true) == true -> {
                        _state.update { it.copy(emailErrorResId = R.string.error_invalid_or_password) }
                    }
                    else -> {
                        _state.update {
                            it.copy(
                                generalErrorMessage = result.message,
                                generalErrorResId = null
                            )
                        }
                    }
                }
            }
            is Resource.Loading -> {  }
        }
    }
}