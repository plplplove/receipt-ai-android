package com.receiptai.tracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The persisted representation of an expense.
 *
 * Amounts are stored in minor currency units (for example, cents) so that
 * calculations never depend on floating point precision.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val merchantName: String,
    val amountMinorUnits: Long,
    val currency: String,
    val dateTimestamp: Long,
    val category: String,
    val notes: String = ""
)
