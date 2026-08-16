package com.receiptai.tracker.domain.repository

interface ReceiptImageStore {
    suspend fun saveReceiptImage(imageBytes: ByteArray): String?

    suspend fun deleteReceiptImage(path: String?)

    suspend fun deleteAllReceiptImages()
}
