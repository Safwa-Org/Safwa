package com.tasneem.safwa.core.local_datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tasneem.safwa.core.models.ProductNode
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM products_table")
    fun getAll() : Flow<List<ProductNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductNode)

    @Delete
    suspend fun delete(product: ProductNode)
}