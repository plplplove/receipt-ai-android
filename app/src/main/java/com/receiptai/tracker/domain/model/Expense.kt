package com.receiptai.tracker.domain.model

data class Expense(
    val id: String,
    val merchantName: String,
    val amountMinorUnits: Long,
    val currency: String,
    val dateTimestamp: Long,
    val category: String,
    val notes: String = ""
)
