package com.tasneem.safwa.features.settings.languageandcurrency.presentation.state

sealed interface LanguageAndCurrencyEffect {
    data object NavigateBack : LanguageAndCurrencyEffect
}