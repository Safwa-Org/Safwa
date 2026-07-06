package com.tasneem.safwa.features.settings.savedaddresses.data.mapper

import com.tasneem.safwa.features.settings.savedaddresses.data.model.AddressEntity
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address


fun AddressEntity.toDomain(): Address = Address(
    id = id,
    label = label,
    recipientName = recipientName,
    street = street,
    cityAndZip = cityAndZip,
    mobileNumber = mobileNumber,
    countryCode = countryCode,
    latitude = latitude,
    longitude = longitude,
    isValidated = isValidated
)

fun Address.toEntity(): AddressEntity = AddressEntity(
    id = id,
    label = label,
    recipientName = recipientName,
    street = street,
    cityAndZip = cityAndZip,
    mobileNumber = mobileNumber,
    countryCode = countryCode,
    latitude = latitude,
    longitude = longitude,
    isValidated = isValidated
)