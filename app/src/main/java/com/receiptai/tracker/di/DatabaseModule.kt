package com.receiptai.tracker.di

import android.content.Context
import androidx.room.Room
import com.receiptai.tracker.data.local.ExpenseDao
import com.receiptai.tracker.data.local.ReceiptAIDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideReceiptAIDatabase(
        @ApplicationContext context: Context
    ): ReceiptAIDatabase = Room.databaseBuilder(
        context,
        ReceiptAIDatabase::class.java,
        "receiptai.db"
    )
        .addMigrations(
            ReceiptAIDatabase.MIGRATION_1_2,
            ReceiptAIDatabase.MIGRATION_2_3,
            ReceiptAIDatabase.MIGRATION_3_4,
            ReceiptAIDatabase.MIGRATION_4_5
        )
        .build()

    @Provides
    fun provideExpenseDao(database: ReceiptAIDatabase): ExpenseDao = database.expenseDao()
}
