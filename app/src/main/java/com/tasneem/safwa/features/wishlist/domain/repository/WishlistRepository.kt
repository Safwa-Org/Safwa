package com.tasneem.safwa.features.wishlist.domain.repository

import com.tasneem.safwa.features.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(): Flow<List<Product>>
    suspend fun toggleFavorite(product: Product)
    suspend fun syncWishlist()
    suspend fun clearWishlist()
}
