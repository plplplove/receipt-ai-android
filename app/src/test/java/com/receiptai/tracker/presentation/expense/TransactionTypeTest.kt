package com.receiptai.tracker.presentation.expense

import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTypeTest {
    @Test
    fun `new transaction defaults to expense`() {
        assertEquals(
            TransactionType.EXPENSE,
            AddEditTransactionUiState().transactionType
        )
    }

    @Test
    fun `type can be switched to income without changing the entered amount`() {
        val state = AddEditTransactionUiState(
            totalAmount = "2500.00",
            transactionType = TransactionType.INCOME
        )

        assertEquals(TransactionType.INCOME, state.transactionType)
        assertEquals("2500.00", state.totalAmount)
    }
}
