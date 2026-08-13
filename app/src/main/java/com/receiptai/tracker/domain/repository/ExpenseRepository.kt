package com.receiptai.tracker.domain.repository

import com.receiptai.tracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(): Flow<List<Expense>>

    suspend fun saveExpense(expense: Expense)

    suspend fun deleteExpense(expenseId: String)

    suspend fun deleteAllExpenses()
}
