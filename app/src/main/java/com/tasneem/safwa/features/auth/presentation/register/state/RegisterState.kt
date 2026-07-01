package com.tasneem.safwa.features.auth.presentation.register.state

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
    val generalErrorResId: Int? = null,
    val showVerificationDialog: Boolean = false
)