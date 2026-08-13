package com.receiptai.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateTimestamp DESC")
    fun observeExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: String)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}
