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

import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.OrderDao
import com.tasneem.safwa.features.settings.orderhistory.data.datasource.local.OrderHistoryEntity

@Database(entities = [WishlistEntity::class, SavedCardEntity::class, OrderHistoryEntity::class], version = 4)
@TypeConverters(SafwaTypeConverters::class)
abstract class SafwaDB : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun savedCardDao(): SavedCardDao
    abstract fun orderDao(): OrderDao
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `order_history_table` (" +
                            "`id` TEXT NOT NULL, " +
                            "`orderNumber` TEXT NOT NULL, " +
                            "`status` TEXT NOT NULL, " +
                            "`images` TEXT NOT NULL, " +
                            "`itemCount` INTEGER NOT NULL, " +
                            "`date` TEXT NOT NULL, " +
                            "`totalPrice` TEXT NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }

        // Renames `images` column to `lineItems` to store rich OrderLineItem objects.
        // SQLite doesn't support RENAME COLUMN on older APIs, so we recreate the table.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `order_history_table_new` (" +
                            "`id` TEXT NOT NULL, " +
                            "`orderNumber` TEXT NOT NULL, " +
                            "`status` TEXT NOT NULL, " +
                            "`lineItems` TEXT NOT NULL, " +
                            "`itemCount` INTEGER NOT NULL, " +
                            "`date` TEXT NOT NULL, " +
                            "`totalPrice` TEXT NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                db.execSQL("DROP TABLE IF EXISTS `order_history_table`")
                db.execSQL("ALTER TABLE `order_history_table_new` RENAME TO `order_history_table`")
            }
        }

        fun getDatabase(context: Context): SafwaDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafwaDB::class.java,
                    "safwa_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}