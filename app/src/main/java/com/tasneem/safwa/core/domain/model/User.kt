package com.tasneem.safwa.core.domain.model

import com.tasneem.safwa.core.data.model.UserEntity

data class User(
    val id: String ,
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
 fun UserEntity.toDomain(): User {
     return User(
         id = id,
         email = email,
         firstName = firstName,
         lastName = lastName,
         phone = phone,
         photoUrl = photoUrl,
         isGuest = isGuest,
         createdAt = createdAt,
         updatedAt = updatedAt
     )
 }