package com.tasneem.safwa.features.settings.languageandcurrency.data.repository

import com.tasneem.safwa.features.settings.languageandcurrency.data.datasource.CurrencyLocalDataSource
import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import com.tasneem.safwa.features.settings.languageandcurrency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val localDataSource: CurrencyLocalDataSource
) : CurrencyRepository {

    override fun getSupportedCurrencies(): Flow<List<Currency>> = flow {
        emit(localDataSource.getLocalCurrencies())
    }
}