package com.tasneem.safwa.core.data.model
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val addresses: List<AddressEntity> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)