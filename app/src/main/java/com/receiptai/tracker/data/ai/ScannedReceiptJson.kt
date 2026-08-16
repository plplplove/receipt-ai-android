package com.receiptai.tracker.data.ai

import com.receiptai.tracker.domain.model.ScannedReceipt
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object ScannedReceiptJson {

    val json = Json { ignoreUnknownKeys = true }

    const val EXTRACTION_PROMPT =
        "You are an AI receipt parser. Analyze the receipt image and extract: " +
            "merchantName (the store name), totalAmount (the grand total as a plain number " +
            "with a dot as the decimal separator, e.g. 14.50), currency (ISO 4217 code, " +
            "e.g. USD), date (purchase date formatted as YYYY-MM-DD), and category " +
            "(exactly one of: Food & Dining, Transport, Shopping, Health, Housing, " +
            "Utilities, Other). Return ONLY a valid JSON object with these exact keys. " +
            "Do not use markdown code blocks and do not add any explanation."

    fun parseScannedReceipt(rawText: String): ScannedReceipt? {
        return try {
            val cleaned = rawText
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val root = json.parseToJsonElement(cleaned).jsonObject
            val merchant = root.stringValue(KEY_MERCHANT)?.trim().orEmpty()
            val amount = root.stringValue(KEY_AMOUNT)?.trim().orEmpty()
            if (merchant.isEmpty() || amount.isEmpty()) return null
            ScannedReceipt(
                merchantName = merchant,
                totalAmount = normalizeAmount(amount),
                currencyCode = root.stringValue(KEY_CURRENCY)?.trim()?.uppercase().orEmpty(),
                dateEpochMillis = parseDate(root.stringValue(KEY_DATE)?.trim()),
                category = root.stringValue(KEY_CATEGORY)?.trim().orEmpty()
            )
        } catch (throwable: Throwable) {
            null
        }
    }

    private fun normalizeAmount(amount: String): String {
        val normalized = amount.replace(',', '.')
        val value = normalized.toDoubleOrNull() ?: return ""
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    private fun parseDate(value: String?): Long? {
        if (value.isNullOrEmpty()) return null
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value)?.time
        }.getOrNull()
    }

    private fun kotlinx.serialization.json.JsonObject.stringValue(key: String): String? =
        runCatching { this[key]?.jsonPrimitive?.content }.getOrNull()

    private const val KEY_MERCHANT = "merchantName"
    private const val KEY_AMOUNT = "totalAmount"
    private const val KEY_CURRENCY = "currency"
    private const val KEY_DATE = "date"
    private const val KEY_CATEGORY = "category"
}
