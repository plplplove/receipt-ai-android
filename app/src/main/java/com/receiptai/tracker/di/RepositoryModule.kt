package com.receiptai.tracker.di

import com.receiptai.tracker.data.ai.FallbackReceiptParser
import com.receiptai.tracker.data.receipts.ReceiptImageStoreImpl
import com.receiptai.tracker.data.repository.ExpenseRepositoryImpl
import com.receiptai.tracker.data.settings.SettingsRepositoryImpl
import com.receiptai.tracker.domain.repository.ExpenseRepository
import com.receiptai.tracker.domain.repository.ReceiptImageStore
import com.receiptai.tracker.domain.repository.ReceiptParser
import com.receiptai.tracker.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        implementation: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindReceiptParser(
        implementation: FallbackReceiptParser
    ): ReceiptParser

    @Binds
    @Singleton
    abstract fun bindReceiptImageStore(
        implementation: ReceiptImageStoreImpl
    ): ReceiptImageStore
}
