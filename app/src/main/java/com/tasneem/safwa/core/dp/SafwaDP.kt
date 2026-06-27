package com.tasneem.safwa.core.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tasneem.safwa.core.local_datasource.ProductsDAO
import com.tasneem.safwa.core.models.ProductNode

@Database(entities = [ProductNode::class], version = 1)
@TypeConverters
abstract class SafwaDP : RoomDatabase() {
    abstract fun ProductsDAO(): ProductsDAO
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