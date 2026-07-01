package com.tasneem.safwa.core.domain.model

data class User(
    val id: String,
    val email: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val addresses: List<Address> = emptyList(), // Added addresses array
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)