package com.tasneem.safwa.core.data.mapper

import com.tasneem.safwa.core.data.model.UserEntity
import com.tasneem.safwa.core.domain.model.User


fun UserEntity.toDomain(): User = User(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest, cartId = cartId,
    customerAccessToken = customerAccessToken,
    createdAt = createdAt, updatedAt = updatedAt
)
fun User.toEntity(): UserEntity = UserEntity(
    id = id, email = email, firstName = firstName, lastName = lastName,
    phone = phone, photoUrl = photoUrl, isGuest = isGuest, cartId = cartId,
    customerAccessToken = customerAccessToken,
    createdAt = createdAt, updatedAt = updatedAt
)