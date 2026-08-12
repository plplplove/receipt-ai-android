package com.receiptai.tracker.presentation.components

import java.math.BigDecimal

fun parsePositiveAmount(value: String): BigDecimal? {
    val normalized = value
        .trim()
        .replace(" ", "")
        .replace(',', '.')
    return normalized.toBigDecimalOrNull()
        ?.takeIf { amount -> amount > BigDecimal.ZERO }
}
