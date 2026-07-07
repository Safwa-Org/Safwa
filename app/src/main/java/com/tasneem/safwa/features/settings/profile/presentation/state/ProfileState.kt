package com.tasneem.safwa.features.settings.profile.presentation.state

data class ProfileState(
    val isLoading: Boolean = false,

    val isGuest: Boolean = false,

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isElite: Boolean = false,

    val ordersCount: Int = 0,
    val wishlistCount: Int = 0,
    val savedAddressesCount: Int = 0,
    val points: Int = 0,

    val language: String = "English",
    val currency: String = "USD",
    val isDarkMode: Boolean = false,

    val showLogoutConfirmDialog: Boolean = false,

    val errorMessage: String? = null
)