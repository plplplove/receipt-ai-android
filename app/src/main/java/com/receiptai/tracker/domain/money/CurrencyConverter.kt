package com.receiptai.tracker.domain.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

/**
 * Converts stored minor units into the user's selected display currency.
 *
 * These are offline reference rates so the UI remains deterministic and
 * usable without network access. A production rate provider can replace this
 * object later without changing presentation or persistence models.
 */
object CurrencyConverter {
    private val referenceRateToUsd = mapOf(
        "USD" to BigDecimal("1.000000"),
        "EUR" to BigDecimal("1.090000"),
        "GBP" to BigDecimal("1.270000"),
        "PLN" to BigDecimal("0.255000"),
        "CAD" to BigDecimal("0.730000"),
        "AUD" to BigDecimal("0.650000"),
        "JPY" to BigDecimal("0.006300")
    )

    fun convertMinorUnits(
        amountMinorUnits: Long,
        fromCurrency: String,
        toCurrency: String
    ): Long {
        val source = fromCurrency.uppercase()
        val target = toCurrency.uppercase()
        if (source == target) return amountMinorUnits

        val sourceRate = referenceRateToUsd[source] ?: return amountMinorUnits
        val targetRate = referenceRateToUsd[target] ?: return amountMinorUnits
        val sourceDigits = fractionDigits(source)
        val targetDigits = fractionDigits(target)

        return BigDecimal.valueOf(amountMinorUnits)
            .movePointLeft(sourceDigits)
            .multiply(sourceRate)
            .divide(targetRate, 12, RoundingMode.HALF_UP)
            .movePointRight(targetDigits)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    private fun fractionDigits(currencyCode: String): Int = runCatching {
        Currency.getInstance(currencyCode).defaultFractionDigits
    }.getOrDefault(2).coerceAtLeast(0)
}
