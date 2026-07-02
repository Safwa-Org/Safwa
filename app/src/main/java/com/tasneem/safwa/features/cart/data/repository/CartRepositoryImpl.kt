package com.tasneem.safwa.features.cart.data.repository

import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {

    override suspend fun createCart(variantId: String, quantity: Int): Resource<String> {
        return try {
            val cartId = remoteDataSource.createCart(variantId, quantity)
            Resource.Success(cartId)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to create cart")
        }
    }

    override suspend fun addToCart(cartId: String, variantId: String, quantity: Int): Resource<Unit> {
        return try {
            remoteDataSource.addToCart(cartId, variantId, quantity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to add item to cart")
        }
    }
}
