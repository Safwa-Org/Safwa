package com.tasneem.safwa.core.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardDao
import com.tasneem.safwa.features.payment.data.datasource.local.SavedCardEntity
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistDao
import com.tasneem.safwa.features.wishlist.data.datasource.local.WishlistEntity

@Database(entities = [WishlistEntity::class, SavedCardEntity::class], version = 2)
@TypeConverters(SafwaTypeConverters::class)
abstract class SafwaDB : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun savedCardDao(): SavedCardDao

    companion object {
        @Volatile
        private var INSTANCE: SafwaDB? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `saved_cards_table` (" +
                            "`id` TEXT NOT NULL, " +
                            "`type` TEXT NOT NULL, " +
                            "`last4` TEXT NOT NULL, " +
                            "`cardholderName` TEXT NOT NULL, " +
                            "`expiryDate` TEXT NOT NULL, " +
                            "`isDefault` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }

        fun getDatabase(context: Context): SafwaDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafwaDB::class.java,
                    "safwa_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}