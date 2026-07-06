package com.tasneem.safwa.core.data.mapper

import com.tasneem.safwa.features.settings.savedaddresses.data.model.AddressEntity
import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.core.domain.model.User
import com.tasneem.safwa.features.settings.savedaddresses.data.mapper.toDomain
import com.tasneem.safwa.features.settings.savedaddresses.data.mapper.toEntity

fun UserEntity.toDomain(): User = User(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest, cartId = cartId,
    customerAccessToken = customerAccessToken,
    addresses = addresses.map { it.toDomain() },
    createdAt = createdAt, updatedAt = updatedAt
)
fun User.toEntity(): UserEntity = UserEntity(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest, cartId = cartId,
    customerAccessToken = customerAccessToken,
    addresses = addresses.map { it.toEntity() },
    createdAt = createdAt, updatedAt = updatedAt
)