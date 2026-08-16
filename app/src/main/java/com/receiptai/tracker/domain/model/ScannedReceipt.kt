package com.receiptai.tracker.domain.model

data class ScannedReceipt(
    val merchantName: String,
    val totalAmount: String,
    val currencyCode: String,
    val dateEpochMillis: Long?,
    val category: String
)
