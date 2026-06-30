package com.tasneem.safwa.features.core.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: WishlistRepository
) {
    suspend operator fun invoke(product: Product): Resource<Unit> {
        return try {
            repository.toggleFavorite(product)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to toggle favorite")
        }
    }
}
