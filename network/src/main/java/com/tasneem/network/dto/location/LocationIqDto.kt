package com.tasneem.network.dto.location

import com.google.gson.annotations.SerializedName

data class LocationIqSuggestionDto(
    @SerializedName("place_id") val placeId: String = "",
    @SerializedName("display_name") val displayName: String = "",
    @SerializedName("lat") val lat: String = "",
    @SerializedName("lon") val lon: String = "",
    @SerializedName("address") val address: LocationIqAddressDto? = null
)

data class LocationIqAddressDto(
    @SerializedName("road") val road: String? = null,
    @SerializedName("house_number") val houseNumber: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("postcode") val postcode: String? = null,
    @SerializedName("country_code") val countryCode: String? = null
)