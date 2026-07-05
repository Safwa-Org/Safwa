package com.tasneem.safwa.features.settings.savedaddresses.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CountryAssetDto(
    val name: String,
    val iso2: String,
    val iso3: String
)