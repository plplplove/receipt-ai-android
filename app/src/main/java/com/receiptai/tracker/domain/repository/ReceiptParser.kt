package com.receiptai.tracker.domain.repository

import com.receiptai.tracker.domain.model.ScannedReceipt

interface ReceiptParser {
    suspend fun parseReceipt(imageBytes: ByteArray): ScannedReceipt?
}
