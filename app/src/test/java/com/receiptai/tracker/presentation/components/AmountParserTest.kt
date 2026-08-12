package com.receiptai.tracker.presentation.components

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountParserTest {
    @Test
    fun `accepts dot and comma decimal separators`() {
        assertEquals(BigDecimal("14.50"), parsePositiveAmount("14.50"))
        assertEquals(BigDecimal("14.50"), parsePositiveAmount("14,50"))
    }

    @Test
    fun `rejects empty non numeric and non positive values`() {
        assertNull(parsePositiveAmount(""))
        assertNull(parsePositiveAmount("CA$ 14"))
        assertNull(parsePositiveAmount("0"))
        assertNull(parsePositiveAmount("-1"))
    }
}
