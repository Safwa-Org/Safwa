package com.tasneem.safwa.features.auth.presentation.register

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val firstNameErrorResId: Int? = null,
    val lastNameErrorResId: Int? = null,
    val emailErrorResId: Int? = null,
    val phoneErrorResId: Int? = null,
    val passwordErrorResId: Int? = null,
    val confirmPasswordErrorResId: Int? = null,
    val generalErrorMessage: String? = null,
    val generalErrorResId: Int? = null
)

sealed interface RegisterEvent {
    data class FirstNameChanged(val firstName: String) : RegisterEvent
    data class LastNameChanged(val lastName: String) : RegisterEvent
    data class EmailChanged(val email: String) : RegisterEvent
    data class PhoneChanged(val phone: String) : RegisterEvent
    data class PasswordChanged(val password: String) : RegisterEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent
    object RegisterClicked : RegisterEvent
    object GoogleSignUpClicked : RegisterEvent
    object LoginClicked : RegisterEvent
}
sealed interface RegisterSideEffect {
    object NavigateToLogin : RegisterSideEffect
    object NavigateToHome : RegisterSideEffect
    data class ShowToast(val message: String? = null, val messageResId: Int? = null) : RegisterSideEffect
}