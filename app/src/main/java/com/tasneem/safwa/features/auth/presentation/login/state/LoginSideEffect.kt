package com.tasneem.safwa.features.auth.presentation.login.state

sealed interface LoginSideEffect {
    object NavigateToSignUp : LoginSideEffect
    object NavigateToForgotPassword : LoginSideEffect
    object NavigateToHome : LoginSideEffect
    data class ShowToast(val message: String? = null, val messageResId: Int? = null) : LoginSideEffect
}