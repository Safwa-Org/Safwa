package com.tasneem.safwa.features.settings.languageandcurrency.presentation.state

sealed interface LanguageAndCurrencyEvent {
    data object LoadPreferences : LanguageAndCurrencyEvent
    data class LanguageSelected(val code: String) : LanguageAndCurrencyEvent
    data class CurrencySelected(val code: String) : LanguageAndCurrencyEvent
    data object SavePreferencesClicked : LanguageAndCurrencyEvent
    data object BackClicked : LanguageAndCurrencyEvent
}