package com.receiptai.tracker.data.ai

import com.receiptai.tracker.domain.model.ScannedReceipt
import com.receiptai.tracker.domain.repository.ReceiptParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackReceiptParser @Inject constructor(
    private val geminiReceiptParser: GeminiReceiptParser,
    private val nvidiaReceiptParser: NvidiaReceiptParser
) : ReceiptParser {

    override suspend fun parseReceipt(imageBytes: ByteArray): ScannedReceipt? {
        return geminiReceiptParser.parseReceipt(imageBytes)
            ?: nvidiaReceiptParser.parseReceipt(imageBytes)
    }
}
