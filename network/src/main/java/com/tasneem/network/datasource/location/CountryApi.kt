package com.tasneem.network.datasource.location

import com.tasneem.network.dto.location.RestCountriesV5ResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryApi {
    @GET("countries/v5")
    suspend fun getAllCountries(
        @Query("response_fields") fields: String = "names.common,codes.alpha_2,codes.alpha_3",
        @Query("sort") sort: String = "names.common"
    ): RestCountriesV5ResponseDto
}