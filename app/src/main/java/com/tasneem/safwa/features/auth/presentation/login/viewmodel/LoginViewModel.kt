package com.tasneem.safwa.features.auth.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasneem.safwa.R
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.usecase.GoogleLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.GuestLoginUseCase
import com.tasneem.safwa.features.auth.domain.usecase.LoginUseCase
import com.tasneem.safwa.features.auth.presentation.login.state.LoginEvent
import com.tasneem.safwa.features.auth.presentation.login.state.LoginSideEffect
import com.tasneem.safwa.features.auth.presentation.login.state.LoginState
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
    private val syncWishlistUseCase: SyncWishlistUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sideEffect = Channel<LoginSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.FormInputChanged -> {
                _state.update {
                    it.copy(
                        email = event.email ?: it.email,
                        password = event.password ?: it.password,
                        emailErrorResId = null,
                        passwordErrorResId = null,
                        generalErrorMessage = null,
                        generalErrorResId = null
                    )
                }
            }

            LoginEvent.LoginClicked -> executeLogin()
            LoginEvent.GoogleLoginClicked -> {}
            LoginEvent.SignUpClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToSignUp) }
            }

            LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _sideEffect.send(LoginSideEffect.NavigateToForgotPassword) }
            }

            LoginEvent.GuestClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    // Uses a Firebase anonymous session so
                    // guests always have a stable UID.
                    val result = guestLoginUseCase()
                    _state.update { it.copy(isLoading = false) }
                    when (result) {
                        is Resource.Success -> _sideEffect.send(LoginSideEffect.NavigateToHome)
                        is Resource.Error -> _state.update {
                            it.copy(generalErrorMessage = result.message)
                        }
                        is Resource.Loading -> {}
                    }
                }
            }

            is LoginEvent.ShowError -> {
                _state.update { it.copy(generalErrorMessage = event.message) }
            }
        }
    }

    private fun executeLogin() {
        val currentState = _state.value
        _state.update {
            it.copy(
                emailErrorResId = null,
                passwordErrorResId = null,
                generalErrorMessage = null,
                generalErrorResId = null,
                isLoading = true
            )
        }
        viewModelScope.launch {
            val result = loginUseCase(currentState.email, currentState.password)
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

    private suspend fun handleAuthResult(result: Resource<User>) {
        _state.update { it.copy(isLoading = false) }
        when (result) {
            is Resource.Success -> {
                syncWishlistUseCase()
                _sideEffect.send(LoginSideEffect.NavigateToHome)
            }

            is Resource.Error -> {
                val message = result.message ?: ""
                when {
                    message == "error_email_empty" -> {
                        _state.update { it.copy(emailErrorResId = R.string.error_email_empty) }
                    }

                    message == "error_password_empty" -> {
                        _state.update { it.copy(passwordErrorResId = R.string.error_password_empty) }
                    }

                    message.contains("error_email_empty", ignoreCase = true) ||
                            message.contains("error_password_empty", ignoreCase = true) -> {
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

            is Resource.Loading -> {}
        }
    }
}
