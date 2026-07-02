package com.tasneem.safwa.features.cart.domain.repository

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.model.Cart

import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartItemCount: StateFlow<Int>
    suspend fun createCart(variantId: String, quantity: Int): Resource<String>
    suspend fun addToCart(cartId: String, variantId: String, quantity: Int): Resource<Unit>
    suspend fun getCart(cartId: String): Resource<Cart>
}
