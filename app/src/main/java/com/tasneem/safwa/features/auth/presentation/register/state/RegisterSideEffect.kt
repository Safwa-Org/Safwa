package com.tasneem.safwa.features.auth.presentation.register.state

sealed interface RegisterSideEffect {
    object NavigateToLogin : RegisterSideEffect
    object NavigateToHome : RegisterSideEffect
    data class ShowToast(val message: String? = null, val messageResId: Int? = null) : RegisterSideEffect
}