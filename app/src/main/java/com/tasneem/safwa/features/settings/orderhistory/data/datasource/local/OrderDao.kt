package com.tasneem.safwa.features.settings.orderhistory.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_history_table")
    fun getOrders(): Flow<List<OrderHistoryEntity>>

    @Query("SELECT * FROM order_history_table WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderHistoryEntity>)

    @Query("DELETE FROM order_history_table")
    suspend fun deleteAllOrders()

    @Query("UPDATE order_history_table SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: String, status: String)
}
