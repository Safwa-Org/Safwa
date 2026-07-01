package com.tasneem.safwa.core.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistDao
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistEntity

@Database(entities = [WishlistEntity::class], version = 1)
@TypeConverters(SafwaTypeConverters::class)
abstract class SafwaDB : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    companion object {
        @Volatile
        private var INSTANCE: SafwaDB? = null

        fun getDatabase(context: Context): SafwaDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafwaDB::class.java,
                    "safwa_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}