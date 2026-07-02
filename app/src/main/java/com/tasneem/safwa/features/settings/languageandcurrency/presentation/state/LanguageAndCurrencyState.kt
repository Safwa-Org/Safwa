package com.tasneem.safwa.features.settings.languageandcurrency.presentation.state

data class LanguageAndCurrencyState(
    val selectedLanguageCode: String = "en",
    val selectedCurrencyCode: String = "USD",
    val isLoading: Boolean = false
)