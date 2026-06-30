package com.tasneem.safwa.features.auth.presentation.login.state

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val emailErrorResId: Int? = null,
    val passwordErrorResId: Int? = null,
    val generalErrorMessage: String? = null,
    val generalErrorResId: Int? = null
)