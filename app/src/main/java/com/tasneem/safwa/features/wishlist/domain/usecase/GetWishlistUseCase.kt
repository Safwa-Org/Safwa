package com.tasneem.safwa.features.wishlist.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.wishlist.domain.model.Product
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository
) {
    operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getWishlist()
            .map { Resource.Success(it) as Resource<List<Product>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "An unexpected error occurred")) }
    }
}
