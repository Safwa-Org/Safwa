package com.tasneem.safwa.features.auth.presentation.login.state

sealed interface LoginEvent {
    data class FormInputChanged(val email: String? = null, val password: String? = null) : LoginEvent
    object LoginClicked : LoginEvent
    object GoogleLoginClicked : LoginEvent
    object SignUpClicked : LoginEvent
    object ForgotPasswordClicked : LoginEvent
    object GuestClicked : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}