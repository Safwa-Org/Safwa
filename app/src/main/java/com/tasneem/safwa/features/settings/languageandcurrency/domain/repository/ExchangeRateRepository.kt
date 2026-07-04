package com.tasneem.safwa.features.settings.languageandcurrency.domain.repository

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.ExchangeRate

interface ExchangeRateRepository {
    suspend fun getExchangeRates(baseCurrency: String): Result<ExchangeRate>
}