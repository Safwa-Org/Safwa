package com.tasneem.safwa.features.wishlist.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_table")
    fun getWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: WishlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<WishlistEntity>)

    @Delete
    suspend fun deleteProduct(product: WishlistEntity)

    @Query("DELETE FROM wishlist_table")
    suspend fun clearWishlist()

    @androidx.room.Transaction
    suspend fun replaceWishlist(products: List<WishlistEntity>) {
        clearWishlist()
        insertProducts(products)
    }
}
