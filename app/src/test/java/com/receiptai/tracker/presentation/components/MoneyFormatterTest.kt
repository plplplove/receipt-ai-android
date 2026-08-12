package com.receiptai.tracker.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyFormatterTest {
    @Test
    fun `formats minor units using currency fraction digits`() {
        assertEquals("\$12.34", formatMoney(1_234L, "USD"))
        assertEquals("¥1,234", formatMoney(1_234L, "JPY"))
    }

    @Test
    fun `preserves negative sign and optionally adds positive sign`() {
        assertEquals("-\$12.34", formatMoney(-1_234L, "USD"))
        assertEquals("+\$12.34", formatMoney(1_234L, "USD", includePositiveSign = true))
    }
}
