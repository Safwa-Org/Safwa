package com.tasneem.safwa.features.home.domain.model

data class UserShoppingHistory(
    val wishlistItems: List<String>,
    val cartItems: List<String>,
    val orderItems: List<String>
)
