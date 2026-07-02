package com.tasneem.safwa.features.cart.domain.repository

import com.tasneem.safwa.core.util.Resource

interface CartRepository {
    suspend fun createCart(variantId: String, quantity: Int): Resource<String>
    suspend fun addToCart(cartId: String, variantId: String, quantity: Int): Resource<Unit>
}
