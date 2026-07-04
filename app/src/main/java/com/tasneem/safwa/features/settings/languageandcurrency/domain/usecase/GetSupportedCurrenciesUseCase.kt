package com.tasneem.safwa.features.settings.languageandcurrency.domain.usecase

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSupportedCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<List<Currency>> {
        return repository.getSupportedCurrencies()
    }
}