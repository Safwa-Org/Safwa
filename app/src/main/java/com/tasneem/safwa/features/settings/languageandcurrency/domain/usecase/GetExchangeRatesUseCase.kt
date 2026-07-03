package com.tasneem.safwa.features.settings.languageandcurrency.domain.usecase

import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.ExchangeRate
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRateRepository
) {
    suspend operator fun invoke(baseCurrency: String = "USD"): Result<ExchangeRate> {
        return repository.getExchangeRates(baseCurrency)
    }
}