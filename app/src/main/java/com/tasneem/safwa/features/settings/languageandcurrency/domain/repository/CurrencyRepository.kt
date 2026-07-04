package com.tasneem.safwa.features.settings.languageandcurrency.domain.repository

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getSupportedCurrencies(): Flow<List<Currency>>
}