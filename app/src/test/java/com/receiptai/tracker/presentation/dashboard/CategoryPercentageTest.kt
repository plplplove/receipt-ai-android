package com.receiptai.tracker.presentation.dashboard

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryPercentageTest {
    @Test
    fun `percentages always total one hundred`() {
        val percentages = calculateCategoryPercentages(listOf(36L, 1L))

        assertEquals(listOf(97, 3), percentages)
        assertEquals(100, percentages.sum())
    }

    @Test
    fun `rounding remainder is assigned without losing a percent`() {
        val percentages = calculateCategoryPercentages(listOf(1L, 1L, 1L))

        assertEquals(listOf(34, 33, 33), percentages)
        assertEquals(100, percentages.sum())
    }

    @Test
    fun `empty category list stays empty`() {
        assertEquals(emptyList<Int>(), calculateCategoryPercentages(emptyList()))
    }
}
