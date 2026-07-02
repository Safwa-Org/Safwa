package com.tasneem.network.datasource.country

import com.tasneem.network.dto.CountryResponseDto
import com.tasneem.network.exception.safeRestApiCall
import javax.inject.Inject

interface CountryRemoteDataSource {
    suspend fun getAllCountries(): CountryResponseDto
}

class CountryRemoteDataSourceImpl @Inject constructor(
    private val countriesApi: CountriesApi
) : CountryRemoteDataSource {
    override suspend fun getAllCountries(): CountryResponseDto {
        return safeRestApiCall {
            countriesApi.getAllCountries()
        }
    }
}