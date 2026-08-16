package com.receiptai.tracker.data.receipts

import android.content.Context
import com.receiptai.tracker.domain.repository.ReceiptImageStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReceiptImageStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ReceiptImageStore {

    private val receiptsDir: File
        get() = File(context.filesDir, RECEIPTS_DIRECTORY).apply { mkdirs() }

    override suspend fun saveReceiptImage(imageBytes: ByteArray): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val file = File(receiptsDir, "${UUID.randomUUID()}$JPEG_EXTENSION")
                file.writeBytes(imageBytes)
                file.absolutePath
            }.getOrNull()
        }

    override suspend fun deleteReceiptImage(path: String?) {
        if (path.isNullOrBlank()) return
        withContext(Dispatchers.IO) {
            runCatching { File(path).delete() }
        }
    }

    override suspend fun deleteAllReceiptImages() {
        withContext(Dispatchers.IO) {
            runCatching {
                receiptsDir.listFiles()?.forEach { it.delete() }
            }
        }
    }

    private companion object {
        const val RECEIPTS_DIRECTORY = "receipts"
        const val JPEG_EXTENSION = ".jpg"
    }
}
