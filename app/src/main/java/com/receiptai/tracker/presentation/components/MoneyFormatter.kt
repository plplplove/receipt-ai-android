package com.receiptai.tracker.presentation.components

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.pow

fun formatMoney(
    amountMinorUnits: Long,
    currencyCode: String,
    includePositiveSign: Boolean = false
): String {
    val currency = runCatching { Currency.getInstance(currencyCode) }
        .getOrDefault(Currency.getInstance("USD"))
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val formatted = NumberFormat.getCurrencyInstance(Locale.US).apply {
        this.currency = currency
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = fractionDigits
    }.format(amountMinorUnits / 10.0.pow(fractionDigits))

    return if (includePositiveSign && amountMinorUnits > 0L) "+$formatted" else formatted
}
