package com.tasneem.safwa.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddressEntity(
    val id: String,
    val label: String,
    val isDefault: Boolean,
    val recipientName: String,
    val street: String,
    val cityAndZip: String,
    val mobileNumber: String
)