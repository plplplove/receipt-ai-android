package com.receiptai.tracker.presentation.settings

import android.content.Context
import android.net.Uri
import com.receiptai.tracker.domain.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val CsvHeader =
    "id,merchant_name,amount_minor_units,currency,date,category,notes"

fun List<Expense>.toReceiptAiCsv(): String = buildString {
    append(CsvHeader)
    append('\n')
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
    this@toReceiptAiCsv.forEach { expense ->
        appendCsvRow(
            expense.id,
            expense.merchantName,
            expense.amountMinorUnits.toString(),
            expense.currency,
            dateFormatter.format(Date(expense.dateTimestamp)),
            expense.category,
            expense.notes
        )
    }
}

fun writeReceiptAiCsv(
    context: Context,
    uri: Uri,
    expenses: List<Expense>
): Result<Unit> = runCatching {
    val outputStream = context.contentResolver.openOutputStream(uri)
        ?: error("Unable to open the selected file")
    outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
        writer.write(expenses.toReceiptAiCsv())
    }
}

private fun StringBuilder.appendCsvRow(vararg values: String) {
    values.forEachIndexed { index, value ->
        if (index > 0) append(',')
        appendCsvValue(value)
    }
    append('\n')
}

private fun StringBuilder.appendCsvValue(value: String) {
    append('"')
    append(value.replace("\"", "\"\""))
    append('"')
}
