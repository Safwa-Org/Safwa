package com.tasneem.safwa.core.data.model
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String = "",
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    @get:com.google.firebase.firestore.PropertyName("isGuest")
    @set:com.google.firebase.firestore.PropertyName("isGuest")
    var isGuest: Boolean = false,
    val cartId: String = "",
    val customerAccessToken: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)