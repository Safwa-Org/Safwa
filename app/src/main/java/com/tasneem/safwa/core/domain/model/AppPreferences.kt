package com.tasneem.safwa.core.domain.model

data class AppPreferences(
    val isDarkMode: Boolean = false,
    val languageCode: String = "en",
    val currencyCode: String = "SAR",
    val notificationsEnabled: Boolean = true,
    val isOnboardingCompleted: Boolean = false
)