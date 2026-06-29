package com.tasneem.safwa.features.authorization.presentation.register

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorResId: Int? = null
)

sealed interface RegisterEvent {
    data class NameChanged(val name: String) : RegisterEvent
    data class EmailChanged(val email: String) : RegisterEvent
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