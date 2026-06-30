package com.tasneem.safwa.core.data.model


data class UserEntity(
    val id: String = "",
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
