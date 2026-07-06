package com.tasneem.safwa.features.settings.savedaddresses.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddressCacheDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val street: String,
    val apartment: String,
    val city: String,
    val province: String,
    val zip: String,
    val countryName: String,
    val countryCode: String,
    val isDefault: Boolean
)
