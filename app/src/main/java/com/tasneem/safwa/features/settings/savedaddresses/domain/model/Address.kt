package com.tasneem.safwa.features.settings.savedaddresses.domain.model


data class Address(
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