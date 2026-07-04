package com.tasneem.safwa.features.settings.languageandcurrency.presentation.state

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency

data class LanguageAndCurrencyState(
    val selectedLanguageCode: String = "en",
    val selectedCurrencyCode: String = "USD",
    val availableCurrencies: List<Currency> = emptyList(),
    val isLoading: Boolean = false
)