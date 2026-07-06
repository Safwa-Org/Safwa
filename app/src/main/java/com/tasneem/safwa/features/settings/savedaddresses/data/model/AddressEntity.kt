package com.tasneem.safwa.features.settings.savedaddresses.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddressEntity(
    val id: String = "",
    val label: String = "",
    val recipientName: String = "",
    val street: String = "",
    val cityAndZip: String = "",
    val mobileNumber: String = "",
    val countryCode: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isValidated: Boolean = false
)