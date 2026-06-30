package com.tasneem.safwa.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.usecase.GetCurrentUserUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GoogleLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GuestLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.LoginUseCase
import com.tasneem.safwa.features.wishlist.domain.usecase.SyncWishlistUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val guestLoginUseCase: GuestLoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val syncWishlistUseCase: SyncWishlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sideEffect = Channel<LoginSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result is Resource.Success && result.data != null) {
                val user = result.data
                _state.update { it.copy(isGuest = user.isGuest) }
                syncWishlistUseCase()
                _sideEffect.send(LoginSideEffect.NavigateToHome)
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
            LoginEvent.LoginClicked -> executeLogin()
            LoginEvent.GoogleLoginClicked -> {  }
            LoginEvent.SignUpClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToSignUp) }
            }
            LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToForgotPassword) }
            }
            LoginEvent.GuestClicked -> executeGuestLogin()
            is LoginEvent.ShowError -> {
                _state.update { it.copy(generalErrorMessage = event.message) }
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
/*        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            return
        }*/
        if (password.isBlank()) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_empty) }
            return
        }
      /*  if (password.length < 6) {
            _state.update { it.copy(passwordErrorResId = R.string.error_password_too_short) }
            return
        }*/

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = loginUseCase(email, password)
            handleAuthResult(result)
        }
    }

    fun handleGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = googleLoginUseCase(idToken)
            handleAuthResult(result)
        }
    }
//        if (currentEmail.isBlank() || currentPassword.isBlank()) {
//            _state.update { it.copy(errorResId = R.string.error_fields_empty) }
//            return
//        }

    private fun executeGuestLogin() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = guestLoginUseCase()
            if (result is Resource.Success) {
                _state.update { it.copy(isGuest = true, isLoading = false) }
                _sideEffect.send(LoginSideEffect.NavigateToHome)
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
                syncWishlistUseCase()
                _sideEffect.send(LoginSideEffect.NavigateToHome)
            }
            is Resource.Error -> {
                val message = result.message ?: ""
                when {
                    message.contains("email", ignoreCase = true) ||
                            message.contains("password", ignoreCase = true) -> {
                        _state.update { it.copy(emailErrorResId = R.string.error_invalid_or_password) }
                    }
                    message.contains("verify", ignoreCase = true) -> {
                        _state.update {
                            it.copy(
                                generalErrorMessage = message,
                                generalErrorResId = null
                            )
                        }
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