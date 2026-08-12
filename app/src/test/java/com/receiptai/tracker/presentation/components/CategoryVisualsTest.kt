package com.receiptai.tracker.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryVisualsTest {
    @Test
    fun `maps supported category labels to stable visual kinds`() {
        assertEquals(ExpenseCategoryKind.FOOD, expenseCategoryKind("Food & Dining"))
        assertEquals(ExpenseCategoryKind.TRANSPORT, expenseCategoryKind("Transport"))
        assertEquals(ExpenseCategoryKind.SHOPPING, expenseCategoryKind("Shopping"))
        assertEquals(ExpenseCategoryKind.HEALTH, expenseCategoryKind("Health"))
        assertEquals(ExpenseCategoryKind.HOUSING, expenseCategoryKind("Housing"))
        assertEquals(ExpenseCategoryKind.UTILITIES, expenseCategoryKind("Utilities"))
        assertEquals(ExpenseCategoryKind.OTHER, expenseCategoryKind("Other"))
    }

    @Test
    fun `recognizes common housing alias`() {
        assertEquals(ExpenseCategoryKind.HOUSING, expenseCategoryKind("Rent"))
    }
}
