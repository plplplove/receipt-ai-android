package com.receiptai.tracker.di

import com.receiptai.tracker.data.repository.ExpenseRepositoryImpl
import com.receiptai.tracker.domain.repository.ExpenseRepository
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
}
