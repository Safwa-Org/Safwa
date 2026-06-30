package com.tasneem.safwa.features.core.domain.usecase

import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import javax.inject.Inject

class ClearWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository
) {
    suspend operator fun invoke() {
        repository.clearWishlist()
    }
}
