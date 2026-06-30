package com.tasneem.safwa.features.auth.presentation.register.state

sealed interface RegisterEvent {
    data class FormInputChanged(
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val password: String? = null,
        val confirmPassword: String? = null
    ) : RegisterEvent

    object RegisterClicked : RegisterEvent
    object LoginClicked : RegisterEvent
    object ResendVerification : RegisterEvent
}