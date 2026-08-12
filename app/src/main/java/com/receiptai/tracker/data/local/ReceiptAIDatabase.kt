package com.receiptai.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ExpenseEntity::class],
    version = 3,
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

        /** Removes records from the old debug-only demo dataset without touching user data. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM expenses WHERE id LIKE 'demo-%'")
            }
        }
    }
}
