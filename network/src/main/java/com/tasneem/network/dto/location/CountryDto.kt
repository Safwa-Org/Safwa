package com.tasneem.network.dto.location

import com.google.gson.annotations.SerializedName

data class RestCountriesV5ResponseDto(
    @SerializedName("data") val data: RestCountriesV5DataDto? = null
)

data class RestCountriesV5DataDto(
    @SerializedName("objects") val objects: List<CountryDto> = emptyList()
)

data class CountryDto(
    @SerializedName("names") val names: CountryNamesDto? = null,
    @SerializedName("codes") val codes: CountryCodesDto? = null
)

data class CountryNamesDto(
    @SerializedName("common") val common: String = ""
)

data class CountryCodesDto(
    @SerializedName("alpha_2") val alpha2: String? = null,
    @SerializedName("alpha_3") val alpha3: String? = null
)