package com.receiptai.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ExpenseEntity::class],
    version = 4,
    exportSchema = false
)
abstract class ReceiptAIDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE expenses ADD COLUMN notes TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        /**
         * Kept as a schema bridge for databases created by the old preview build.
         * It must remain non-destructive: user-created rows can use any id format.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No data cleanup belongs in a schema migration.
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Reserved for the next schema change; intentionally non-destructive.
            }
        }
    }
}
