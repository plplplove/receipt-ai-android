package com.receiptai.tracker.data.repository

import com.receiptai.tracker.data.local.ExpenseDao
import com.receiptai.tracker.data.local.ExpenseEntity
import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    override fun observeExpenses(): Flow<List<Expense>> =
        expenseDao.observeExpenses().map { expenses -> expenses.map(ExpenseEntity::toDomain) }

    override suspend fun saveExpense(expense: Expense) {
        expenseDao.upsert(expense.toEntity())
    }

    override suspend fun deleteExpense(expenseId: String) {
        expenseDao.deleteById(expenseId)
    }
}

private fun ExpenseEntity.toDomain() = Expense(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = amountMinorUnits,
    currency = currency,
    dateTimestamp = dateTimestamp,
    category = category
)

private fun Expense.toEntity() = ExpenseEntity(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = amountMinorUnits,
    currency = currency,
    dateTimestamp = dateTimestamp,
    category = category
)
