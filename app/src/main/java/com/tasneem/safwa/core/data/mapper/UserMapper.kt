package com.tasneem.safwa.core.data.mapper

import com.tasneem.safwa.core.data.model.AddressEntity
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.domain.model.Address
import com.tasneem.safwa.core.domain.model.User

fun AddressEntity.toDomain(): Address = Address(
    id = id, label = label, isDefault = isDefault,
    recipientName = recipientName, street = street,
    cityAndZip = cityAndZip, mobileNumber = mobileNumber
)

fun Address.toEntity(): AddressEntity = AddressEntity(
    id = id, label = label, isDefault = isDefault,
    recipientName = recipientName, street = street,
    cityAndZip = cityAndZip, mobileNumber = mobileNumber
)

fun UserEntity.toDomain(): User = User(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest,
    addresses = addresses.map { it.toDomain() },
    createdAt = createdAt, updatedAt = updatedAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest,
    addresses = addresses.map { it.toEntity() },
    createdAt = createdAt, updatedAt = updatedAt
)