package com.tasneem.safwa.features.settings.profile.presentation.state

data class ProfileState(
    val isLoading: Boolean = false,

    // User Details
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isElite: Boolean = false,

    // Stats & Summaries
    val ordersCount: Int = 0,
    val wishlistCount: Int = 0,
    val savedAddressesCount: Int = 0,
    val points: Int = 0,

    // Settings Preferences
    val language: String = "English",
    val currency: String = "USD",
    val isDarkMode: Boolean = false,

    // Error Handling
    val errorMessage: String? = null
)