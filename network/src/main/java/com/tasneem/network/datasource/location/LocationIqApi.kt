package com.tasneem.network.datasource.location

import com.tasneem.network.dto.location.LocationIqSuggestionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationIqApi {
    @GET("v1/autocomplete")
    suspend fun autocomplete(
        @Query("q") query: String,
        @Query("countrycodes") countryCodeIso2: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 8,
        @Query("key") apiKey: String
    ): List<LocationIqSuggestionDto>
}