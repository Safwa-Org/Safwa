package com.tasneem.safwa.features.wishlist.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import javax.inject.Inject

class SyncWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            repository.syncWishlist()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to sync wishlist")
        }
    }
}
