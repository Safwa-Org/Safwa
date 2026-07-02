package com.tasneem.safwa.features.payment.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCardDao {
    @Query("SELECT * FROM saved_cards_table")
    fun getSavedCards(): Flow<List<SavedCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: SavedCardEntity)

    @Delete
    suspend fun deleteCard(card: SavedCardEntity)
}
