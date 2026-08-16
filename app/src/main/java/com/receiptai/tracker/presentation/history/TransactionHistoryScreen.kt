package com.receiptai.tracker.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.components.categoryVisualStyle
import com.receiptai.tracker.presentation.components.formatMoney
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface

private val HistoryBackground: Color
    @Composable get() = ReceiptAIBackground
private val SearchBackground: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

data class HistoryTransaction(
    val id: String,
    val dateGroup: String,
    val merchantName: String,
    val category: String,
    val amountMinorUnits: Long,
    val currency: String = "USD",
    val originalAmountMinorUnits: Long = amountMinorUnits,
    val originalCurrency: String = currency
)

@Composable
fun TransactionHistoryScreen(
    modifier: Modifier = Modifier,
    transactions: List<HistoryTransaction> = emptyList(),
    onDestinationSelected: (DashboardDestination) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onTransactionClick: (HistoryTransaction) -> Unit = {}
) {
    val strings = receiptAIStrings()
    var query by rememberSaveable { mutableStateOf("") }
    var isFilterSheetVisible by rememberSaveable { mutableStateOf(false) }
    var appliedFilters by remember { mutableStateOf(TransactionFilterState()) }
    val filteredTransactions = transactions.filter { transaction ->
        (query.isBlank() || listOf(
            transaction.merchantName,
            transaction.category,
            transaction.dateGroup
        ).any { value -> value.contains(query, ignoreCase = true) }) &&
            transaction.matches(appliedFilters)
    }
    val groupedTransactions = filteredTransactions.groupBy { it.dateGroup }
    val groupOrder = listOf("Today", "Yesterday", "This Week", "Older")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = HistoryBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ReceiptAIBottomBar(
                selectedDestination = DashboardDestination.HISTORY,
                onDestinationSelected = onDestinationSelected,
                onAddClick = onAddExpenseClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { AppSectionHeader(title = strings.transactions) }
            item {
                HistorySearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onFilterClick = {
                        isFilterSheetVisible = true
                    }
                )
            }

            groupOrder.forEach { groupName ->
                val groupTransactions = groupedTransactions[groupName].orEmpty()
                if (groupTransactions.isNotEmpty()) {
                    item(key = "header-$groupName") {
                        Text(
                            text = strings.dateGroupLabel(groupName),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = ReceiptAISecondaryText,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    items(
                        items = groupTransactions,
                        key = { transaction -> transaction.id }
                    ) { transaction ->
                        HistoryTransactionCard(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction) }
                        )
                    }
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    EmptyHistoryState(hasTransactions = transactions.isNotEmpty())
                }
            }
        }
    }

    if (isFilterSheetVisible) {
        TransactionFilterBottomSheet(
            initialFilters = appliedFilters,
            onDismissRequest = { isFilterSheetVisible = false },
            onApply = { filters ->
                appliedFilters = filters
                isFilterSheetVisible = false
            }
        )
    }
}

@Composable
private fun HistorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    val strings = receiptAIStrings()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = {
                Text(
                    text = strings.searchTransactions,
                    color = ReceiptAISecondaryText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ReceiptAISecondaryText
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SearchBackground,
                unfocusedContainerColor = SearchBackground,
                disabledContainerColor = SearchBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ReceiptAISurface)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = strings.filterTransactions,
                tint = ReceiptAIDeepPurple
            )
        }
    }
}

@Composable
private fun HistoryTransactionCard(
    transaction: HistoryTransaction,
    onClick: () -> Unit
) {
    val strings = receiptAIStrings()
    val iconStyle = categoryVisualStyle(transaction.category)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconStyle.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconStyle.icon,
                    contentDescription = null,
                    tint = iconStyle.accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptAIPrimaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = strings.categoryLabel(transaction.category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(
                        transaction.amountMinorUnits,
                        transaction.currency,
                        includePositiveSign = true
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amountMinorUnits < 0) {
                        Color(0xFFC62828)
                    } else {
                        Color(0xFF2E7D52)
                    }
                )
                if (transaction.originalCurrency != transaction.currency) {
                    Text(
                        text = formatMoney(
                            transaction.originalAmountMinorUnits,
                            transaction.originalCurrency,
                            includePositiveSign = true
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = ReceiptAISecondaryText.copy(alpha = 0.68f)
                    )
                }
            }
        }
    }
}

private fun HistoryTransaction.matches(filters: TransactionFilterState): Boolean {
    val typeMatches = when (filters.type) {
        TransactionTypeFilter.ALL -> true
        TransactionTypeFilter.EXPENSES -> amountMinorUnits < 0
        TransactionTypeFilter.INCOME -> amountMinorUnits >= 0
    }
    val categoryMatches = filters.category == "All" || category == filters.category
    val dateMatches = when (filters.date) {
        TransactionDateFilter.ALL_TIME -> true
        TransactionDateFilter.TODAY -> dateGroup == "Today"
        TransactionDateFilter.THIS_WEEK -> dateGroup in setOf("Today", "Yesterday", "This Week")
    }
    return typeMatches && categoryMatches && dateMatches
}

@Composable
private fun EmptyHistoryState(hasTransactions: Boolean) {
    val strings = receiptAIStrings()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasTransactions) {
                strings.noMatchingTransactions
            } else {
                strings.noTransactions
            },
            color = ReceiptAISecondaryText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransactionHistoryScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme() {
        TransactionHistoryScreen()
    }
}
