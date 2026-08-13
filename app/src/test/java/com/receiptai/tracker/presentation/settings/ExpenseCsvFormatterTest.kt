package com.receiptai.tracker.presentation.settings

import com.receiptai.tracker.domain.model.Expense
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseCsvFormatterTest {
    @Test
    fun `csv contains precise amounts and escapes commas and quotes`() {
        val csv = listOf(
            Expense(
                id = "expense-1",
                merchantName = "Cafe, \"Main\"",
                amountMinorUnits = -1250L,
                currency = "USD",
                dateTimestamp = 0L,
                category = "Food & Dining",
                notes = "Team lunch, paid by me"
            )
        ).toReceiptAiCsv()

        assertTrue(csv.startsWith("id,merchant_name,amount_minor_units,currency,date,category,notes"))
        assertTrue(csv.contains("\"Cafe, \"\"Main\"\"\""))
        assertTrue(csv.contains("\"-1250\""))
        assertTrue(csv.contains("\"Team lunch, paid by me\""))
    }
}
