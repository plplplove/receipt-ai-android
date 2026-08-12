package com.receiptai.tracker.presentation.dashboard

import com.receiptai.tracker.presentation.expense.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionAmountSignTest {
    @Test
    fun `expense is stored as a negative amount`() {
        assertEquals(
            -12_500L,
            signedAmountMinorUnits(12_500L, TransactionType.EXPENSE)
        )
    }

    @Test
    fun `income is stored as a positive amount`() {
        assertEquals(
            12_500L,
            signedAmountMinorUnits(12_500L, TransactionType.INCOME)
        )
    }
}
