package com.tasneem.safwa.features.wishlist.data.repository

import com.tasneem.safwa.features.core.domain.usecase.GetCurrentUserIdUseCase
import com.tasneem.safwa.features.wishlist.data.datasource.firebase.WishlistRemoteDataSource
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistDao
import com.tasneem.safwa.features.wishlist.data.datasource.local.toEntity
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.settings.languageandcurrency.data.CurrencyRateManager
import com.tasneem.safwa.features.wishlist.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val localDataSource: WishlistDao,
    private val remoteDataSource: WishlistRemoteDataSource,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val currencyRateManager: CurrencyRateManager
) : WishlistRepository {

    override fun getWishlist(): Flow<List<Product>> {
        return combine(
            localDataSource.getWishlist(),
            currencyRateManager.displayCurrency
        ) { entities, _ ->
            entities.map { entity ->
                val product = entity.toDomainModel()
                val baseAmount = product.price.toBigDecimalOrNull() ?: return@map product
                product.copy(
                    price = currencyRateManager.convertFromBase(baseAmount).toPlainString(),
                    currency = currencyRateManager.currentDisplayCurrency()
                )
            }
        }
    }
    override suspend fun toggleFavorite(product: Product) {
        val currentWishlist = localDataSource.getWishlist().firstOrNull() ?: emptyList()
        val isFavorite = currentWishlist.any { it.id == product.id }
        
        val userId = getCurrentUserIdUseCase()

        if (isFavorite) {
            localDataSource.deleteProduct(product.toEntity())
            if (userId != null) {
                remoteDataSource.removeProduct(userId, product.id)
            }
        } else {
            localDataSource.insertProduct(product.toEntity())
            if (userId != null) {
                remoteDataSource.addProduct(userId, product)
            }
        }
    }

    override suspend fun syncWishlist() {
        val userId = getCurrentUserIdUseCase() ?: return
        
        val remoteProducts = remoteDataSource.getWishlist(userId)
        
        val localEntities = localDataSource.getWishlist().firstOrNull() ?: emptyList()
        val localProducts = localEntities.map { it.toDomainModel() }
        
        val strictlyLocal = localProducts.filter { localP -> remoteProducts.none { it.id == localP.id } }
        for (product in strictlyLocal) {
            remoteDataSource.addProduct(userId, product)
        }
        
        val combinedProducts = remoteProducts + strictlyLocal
        
        localDataSource.replaceWishlist(combinedProducts.map { it.toEntity() })
    }

    override suspend fun clearWishlist() {
        localDataSource.clearWishlist()
    }
}
