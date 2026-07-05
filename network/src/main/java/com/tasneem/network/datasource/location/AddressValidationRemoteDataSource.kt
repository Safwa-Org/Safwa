package com.tasneem.network.datasource.location

import com.tasneem.network.dto.location.LocationIqSuggestionDto
import com.tasneem.safwa.network.BuildConfig
import javax.inject.Inject

interface AddressValidationRemoteDataSource {
    suspend fun getSuggestions(query: String, countryCodeIso2: String): List<LocationIqSuggestionDto>
}

class AddressValidationRemoteDataSourceImpl @Inject constructor(
    private val api: LocationIqApi
) : AddressValidationRemoteDataSource {
    override suspend fun getSuggestions(
        query: String,
        countryCodeIso2: String
    ): List<LocationIqSuggestionDto> {
        return api.autocomplete(
            query = query,
            countryCodeIso2 = countryCodeIso2.lowercase(),
            apiKey = BuildConfig.LOCATIONIQ_API_KEY
        )
    }
}