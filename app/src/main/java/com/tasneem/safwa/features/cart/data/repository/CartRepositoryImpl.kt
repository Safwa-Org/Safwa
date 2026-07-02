package com.tasneem.safwa.features.cart.data.repository

import com.tasneem.network.datasource.cart.CartRemoteDataSource
import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.cart.domain.repository.CartRepository
import com.tasneem.safwa.features.cart.data.mapper.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {

    private val _cartItemCount = MutableStateFlow(0)
    override val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    override suspend fun createCart(variantId: String, quantity: Int): Resource<String> {
        return try {
            val cartId = remoteDataSource.createCart(variantId, quantity)
            _cartItemCount.value = quantity
            Resource.Success(cartId)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to create cart")
        }
    }

    override suspend fun addToCart(cartId: String, variantId: String, quantity: Int): Resource<Unit> {
        return try {
            remoteDataSource.addToCart(cartId, variantId, quantity)
            _cartItemCount.value += quantity
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to add item to cart")
        }
    }

    override suspend fun getCart(cartId: String): Resource<com.tasneem.safwa.features.cart.domain.model.Cart> {
        return try {
            val dto = remoteDataSource.getCart(cartId)
            _cartItemCount.value = dto.lines.sumOf { it.quantity }
            Resource.Success(dto.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch cart")
        }
    }
}
