package com.tasneem.network.datasource.country

import com.tasneem.network.dto.CountryResponseDto
import com.tasneem.safwa.network.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CountriesApi {
    @GET("countries/v5")
    suspend fun getAllCountries(
        @Header("Authorization") token: String = "Bearer ${BuildConfig.COUNTRIES_API_KEY}"
    ): CountryResponseDto
}