package com.receiptai.tracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val merchantName: String,
    val amountMinorUnits: Long,
    val currency: String,
    val dateTimestamp: Long,
    val category: String,
    val notes: String = "",
    val receiptImagePath: String? = null
)
