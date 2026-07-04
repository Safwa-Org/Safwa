package com.tasneem.network.dto

import com.google.gson.annotations.SerializedName

data class ExchangeRateDto(
    @SerializedName("base") val baseCurrency: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)