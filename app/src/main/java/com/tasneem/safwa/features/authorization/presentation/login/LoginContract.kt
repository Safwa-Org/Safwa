package com.tasneem.safwa.features.authorization.presentation.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailErrorResId: Int? = null,
    val passwordErrorResId: Int? = null,
    val generalErrorMessage: String? = null,
    val generalErrorResId: Int? = null
)

sealed interface LoginSideEffect {
    object NavigateToSignUp : LoginSideEffect
    object NavigateToForgotPassword : LoginSideEffect
    object NavigateToHome : LoginSideEffect
    object NavigateAsGuest : LoginSideEffect
    data class ShowToast(val message: String? = null, val messageResId: Int? = null) : LoginSideEffect
}

sealed interface LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    object LoginClicked : LoginEvent
    object GoogleLoginClicked : LoginEvent
    object SignUpClicked : LoginEvent
    object ForgotPasswordClicked : LoginEvent
    object GuestClicked : LoginEvent
}