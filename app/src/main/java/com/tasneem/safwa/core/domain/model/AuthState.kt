package com.tasneem.safwa.core.domain.model

sealed interface AuthState {

    object Loading : AuthState

    data class Guest(val user: User) : AuthState

    data class Authenticated(val user: User) : AuthState
}
