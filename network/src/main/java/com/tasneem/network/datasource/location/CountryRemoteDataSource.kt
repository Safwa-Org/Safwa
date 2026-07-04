package com.tasneem.network.datasource.location

import com.tasneem.network.dto.location.CountryDto
import javax.inject.Inject

interface CountryRemoteDataSource {
    suspend fun getCountries(): List<CountryDto>
}

class CountryRemoteDataSourceImpl @Inject constructor(
    private val countryApi: CountryApi
) : CountryRemoteDataSource {
    override suspend fun getCountries(): List<CountryDto> =
        countryApi.getAllCountries().data?.objects ?: emptyList()
}