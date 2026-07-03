package com.tasneem.network.datasource.currency

import com.tasneem.network.datasource.currency.ExchangeRateApi
import com.tasneem.network.dto.ExchangeRateDto
import com.tasneem.network.exception.safeRestApiCall
import javax.inject.Inject

interface ExchangeRateRemoteDataSource {
    suspend fun getLatestRates(baseCurrency: String): ExchangeRateDto
}

class ExchangeRateRemoteDataSourceImpl @Inject constructor(
    private val api: ExchangeRateApi
) : ExchangeRateRemoteDataSource {

    override suspend fun getLatestRates(baseCurrency: String): ExchangeRateDto {
        return safeRestApiCall {
            api.getLatestRates(baseCurrency)
        }
    }
}