package com.tasneem.network.datasource.cart

interface CartRemoteDataSource {
    suspend fun createCart(variantId: String, quantity: Int): String
    suspend fun addToCart(cartId: String, variantId: String, quantity: Int)
}
