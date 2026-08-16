@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.receiptai.tracker.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISystemBarsEffect
import com.receiptai.tracker.presentation.localization.receiptAIStrings

enum class TransactionTypeFilter {
    ALL,
    EXPENSES,
    INCOME
}

enum class TransactionDateFilter {
    ALL_TIME,
    TODAY,
    THIS_WEEK
}

data class TransactionFilterState(
    val type: TransactionTypeFilter = TransactionTypeFilter.ALL,
    val category: String = "All",
    val date: TransactionDateFilter = TransactionDateFilter.ALL_TIME
)

@Composable
fun TransactionFilterBottomSheet(
    initialFilters: TransactionFilterState,
    onDismissRequest: () -> Unit,
    onApply: (TransactionFilterState) -> Unit
) {
    val strings = receiptAIStrings()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var filters by remember(initialFilters) { mutableStateOf(initialFilters) }
    val categories = listOf(
        "All",
        "Food & Dining",
        "Transport",
        "Shopping",
        "Health",
        "Housing",
        "Utilities",
        "Other"
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = ReceiptAISurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        ReceiptAISystemBarsEffect(
            statusBarColor = ReceiptAIBackground,
            navigationBarColor = ReceiptAISurface
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.filterTransactions,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.filterTransactions,
                        tint = ReceiptAISecondaryText
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            FilterSectionTitle(strings.filterTransactionType)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionTypeFilter.entries.forEach { type ->
                    FilterChip(
                        selected = filters.type == type,
                        onClick = { filters = filters.copy(type = type) },
                        label = { Text(type.label()) },
                        modifier = Modifier.weight(1f),
                        colors = filterChipColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            FilterSectionTitle(strings.category)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = filters.category == category,
                        onClick = { filters = filters.copy(category = category) },
                        label = { Text(strings.categoryLabel(category)) },
                        colors = filterChipColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            FilterSectionTitle(strings.date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionDateFilter.entries.forEach { date ->
                    FilterChip(
                        selected = filters.date == date,
                        onClick = { filters = filters.copy(date = date) },
                        label = { Text(date.label()) },
                        modifier = Modifier.weight(1f),
                        colors = filterChipColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { filters = TransactionFilterState() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(strings.clear)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { onApply(filters) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(strings.apply)
                }
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = ReceiptAIPrimaryText,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
    selectedLabelColor = MaterialTheme.colorScheme.primary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    labelColor = ReceiptAISecondaryText
)

@Composable
private fun TransactionTypeFilter.label() = when (this) {
    TransactionTypeFilter.ALL -> receiptAIStrings().all
    TransactionTypeFilter.EXPENSES -> receiptAIStrings().expenses
    TransactionTypeFilter.INCOME -> receiptAIStrings().income
}

@Composable
private fun TransactionDateFilter.label() = when (this) {
    TransactionDateFilter.ALL_TIME -> receiptAIStrings().allTime
    TransactionDateFilter.TODAY -> receiptAIStrings().today
    TransactionDateFilter.THIS_WEEK -> receiptAIStrings().thisWeek
}
