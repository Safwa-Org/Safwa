package com.tasneem.network.dto

import com.google.gson.annotations.SerializedName


data class CountryResponseDto(
    @SerializedName("data") val data: CountryDataWrapper?
)

data class CountryDataWrapper(
    @SerializedName("objects") val objects: List<CountryDto>?
)

data class CountryDto(
    @SerializedName("names") val names: CountryNameDto?,
    @SerializedName("codes") val codes: CountryCodesDto?,
    @SerializedName("flag") val flag: CountryFlagsDto?,
    // v5 API returns this as an array, not an object/map
    @SerializedName("currencies") val currencies: List<CurrencyDto>?
)

data class CountryNameDto(
    @SerializedName("common") val common: String?
)

data class CountryCodesDto(
    @SerializedName("alpha_2") val alpha2: String?
)

data class CurrencyDto(
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("symbol") val symbol: String?
)

data class CountryFlagsDto(
    @SerializedName("url_svg") val svg: String?,
    @SerializedName("url_png") val png: String?
)