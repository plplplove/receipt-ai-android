package com.receiptai.tracker.data.repository

import com.receiptai.tracker.data.local.ExpenseDao
import com.receiptai.tracker.data.local.ExpenseEntity
import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.domain.repository.ExpenseRepository
import com.receiptai.tracker.domain.repository.ReceiptImageStore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val receiptImageStore: ReceiptImageStore
) : ExpenseRepository {
    override fun observeExpenses(): Flow<List<Expense>> =
        expenseDao.observeExpenses().map { expenses -> expenses.map(ExpenseEntity::toDomain) }

    override suspend fun saveExpense(expense: Expense) {
        val existingPath = expenseDao.getById(expense.id)?.receiptImagePath
        if (existingPath != null && existingPath != expense.receiptImagePath) {
            receiptImageStore.deleteReceiptImage(existingPath)
        }
        expenseDao.upsert(expense.toEntity())
    }

    override suspend fun deleteExpense(expenseId: String) {
        expenseDao.getById(expenseId)?.let { expenseEntity ->
            receiptImageStore.deleteReceiptImage(expenseEntity.receiptImagePath)
        }
        expenseDao.deleteById(expenseId)
    }

    override suspend fun deleteAllExpenses() {
        receiptImageStore.deleteAllReceiptImages()
        expenseDao.deleteAll()
    }
}

private fun ExpenseEntity.toDomain() = Expense(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = amountMinorUnits,
    currency = currency,
    dateTimestamp = dateTimestamp,
    category = category,
    notes = notes,
    receiptImagePath = receiptImagePath
)

private fun Expense.toEntity() = ExpenseEntity(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = amountMinorUnits,
    currency = currency,
    dateTimestamp = dateTimestamp,
    category = category,
    notes = notes,
    receiptImagePath = receiptImagePath
)
