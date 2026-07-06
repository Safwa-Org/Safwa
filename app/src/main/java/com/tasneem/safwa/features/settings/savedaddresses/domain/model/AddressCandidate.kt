package com.tasneem.safwa.features.settings.savedaddresses.domain.model

data class AddressCandidate(
    val id: String,
    val displayLabel: String,
    val street: String?,
    val city: String,
    val zip: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double
)