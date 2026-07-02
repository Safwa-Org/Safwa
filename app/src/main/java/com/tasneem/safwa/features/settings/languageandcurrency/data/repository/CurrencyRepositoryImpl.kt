package com.tasneem.safwa.features.settings.languageandcurrency.data.repository

import com.tasneem.network.datasource.country.CountryRemoteDataSource
import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
private val remoteDataSource: CountryRemoteDataSource
) : CurrencyRepository {

    override fun getSupportedCurrencies(): Flow<List<Currency>> = flow {
        val response = remoteDataSource.getAllCountries()

        val domainList = response.data?.objects?.mapNotNull { dto ->
            val currency = dto.currencies?.firstOrNull() ?: return@mapNotNull null

            Currency(
                code = currency.code ?: "Unknown",
                name = currency.name ?: "Unknown",
                flagUrl = dto.flag?.png ?: ""
            )
        } ?: emptyList()

        emit(domainList)
    }
}