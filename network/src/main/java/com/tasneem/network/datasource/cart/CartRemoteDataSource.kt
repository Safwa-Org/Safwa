package com.tasneem.network.datasource.cart

import com.tasneem.network.dto.ApplyDiscountResultDto
import com.tasneem.network.dto.CartDto

interface CartRemoteDataSource {
    suspend fun createCart(variantId: String, quantity: Int): String
    suspend fun addToCart(cartId: String, variantId: String, quantity: Int)
    suspend fun getCart(cartId: String): CartDto
    suspend fun removeFromCart(cartId: String, lineIds: List<String>): String
    suspend fun updateCartLine(cartId: String, lineId: String, quantity: Int): String
    suspend fun applyDiscountCode(cartId: String, discountCodes: List<String>): ApplyDiscountResultDto
}
