package com.tasneem.safwa.features.settings.languageandcurrency.domain.model

data class ExchangeRate(
    val baseCurrency: String,
    val date: String,
    val rates: Map<String, Double>
)