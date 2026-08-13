package com.receiptai.tracker.domain.money

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyConverterTest {
    @Test
    fun sameCurrencyKeepsTheExactStoredMinorUnits() {
        assertEquals(
            6_500L,
            CurrencyConverter.convertMinorUnits(6_500L, "PLN", "PLN")
        )
    }

    @Test
    fun convertsPlnToEurUsingTheReferenceRate() {
        assertEquals(
            1_521L,
            CurrencyConverter.convertMinorUnits(6_500L, "PLN", "EUR")
        )
    }
}
