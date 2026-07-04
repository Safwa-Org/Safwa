package com.tasneem.safwa.features.settings.languageandcurrency.data.repository

import com.tasneem.network.datasource.currency.ExchangeRateRemoteDataSource
import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.ExchangeRate
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExchangeRateRemoteDataSource
) : ExchangeRateRepository {

    override suspend fun getExchangeRates(baseCurrency: String): Result<ExchangeRate> {
        return try {
            val dto = remoteDataSource.getLatestRates(baseCurrency)

            val domainModel = ExchangeRate(
                baseCurrency = dto.baseCurrency ?: baseCurrency,
                date = dto.date ?: "",
                rates = dto.rates ?: emptyMap()
            )

            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}