package com.tasneem.safwa.core.domain.model

import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address

data class User(
    val id: String,
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val cartId: String = "",
    val customerAccessToken: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)