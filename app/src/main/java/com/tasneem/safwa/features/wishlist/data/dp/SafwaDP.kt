package com.tasneem.safwa.features.wishlist.data.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistDao
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistEntity

@Database(entities = [WishlistEntity::class], version = 1)
@TypeConverters(SafwaTypeConverters::class)
abstract class SafwaDP : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    companion object {
        @Volatile
        private var INSTANCE: SafwaDP? = null

        fun getDatabase(context: Context): SafwaDP {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafwaDP::class.java,
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