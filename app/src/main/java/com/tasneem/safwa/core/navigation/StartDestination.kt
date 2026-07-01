package com.tasneem.safwa.core.navigation

sealed interface StartDestination {
    object Loading : StartDestination
    object Home : StartDestination
    object Onboarding : StartDestination
}
