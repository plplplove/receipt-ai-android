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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

private val HistoryBackground = ReceiptAIBackground
private val SearchBackground = Color(0xFFF0EEF3)

data class HistoryTransaction(
    val id: String,
    val dateGroup: String,
    val merchantName: String,
    val category: String,
    val amountMinorUnits: Long,
    val currency: String = "USD"
)

@Composable
fun TransactionHistoryScreen(
    transactions: List<HistoryTransaction> = emptyList(),
    onDestinationSelected: (DashboardDestination) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
    val groupOrder = listOf("Today", "Yesterday", "This Week")

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
            item { AppSectionHeader(title = "Transactions") }
            item {
                HistorySearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onFilterClick = {
                        onFilterClick()
                        isFilterSheetVisible = true
                    }
                )
            }

            groupOrder.forEach { groupName ->
                val groupTransactions = groupedTransactions[groupName].orEmpty()
                if (groupTransactions.isNotEmpty()) {
                    item(key = "header-$groupName") {
                        Text(
                            text = groupName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4F4B59),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    items(
                        items = groupTransactions,
                        key = { transaction -> transaction.id }
                    ) { transaction ->
                        HistoryTransactionCard(transaction = transaction)
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
                    text = "Search transactions...",
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
                .background(Color.White)
                .border(1.dp, Color(0xFFD6CFDF), RoundedCornerShape(16.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filter transactions",
                tint = ReceiptAIDeepPurple
            )
        }
    }
}

@Composable
private fun HistoryTransactionCard(transaction: HistoryTransaction) {
    val iconStyle = transactionIconStyle(transaction.category)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconStyle.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconStyle.icon,
                    contentDescription = null,
                    tint = iconStyle.tint,
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
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
            }
            Text(
                text = formatHistoryAmount(
                    transaction.amountMinorUnits,
                    transaction.currency
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.amountMinorUnits < 0) {
                    Color(0xFFC62828)
                } else {
                    Color(0xFF2E7D52)
                }
            )
        }
    }
}

private data class TransactionIconStyle(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun transactionIconStyle(category: String): TransactionIconStyle = when {
    category.contains("food", ignoreCase = true) -> TransactionIconStyle(
        icon = Icons.Default.LocalCafe,
        background = Color(0xFFE2F6EF),
        tint = Color(0xFF008F7A)
    )
    category.contains("transport", ignoreCase = true) -> TransactionIconStyle(
        icon = Icons.Default.DirectionsCar,
        background = Color(0xFFF0EBF8),
        tint = ReceiptAIDeepPurple
    )
    category.contains("shopping", ignoreCase = true) -> TransactionIconStyle(
        icon = Icons.Default.ShoppingBag,
        background = Color(0xFFEDE7F6),
        tint = ReceiptAIDeepPurple
    )
    category.contains("health", ignoreCase = true) -> TransactionIconStyle(
        icon = Icons.Default.Favorite,
        background = Color(0xFFE4F4EC),
        tint = Color(0xFF2E7D52)
    )
    else -> TransactionIconStyle(
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        background = Color(0xFFF0EEF3),
        tint = ReceiptAIDeepPurple
    )
}

private fun formatHistoryAmount(amountMinorUnits: Long, currencyCode: String): String {
    val currency = runCatching { Currency.getInstance(currencyCode) }
        .getOrDefault(Currency.getInstance("USD"))
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val amount = NumberFormat.getCurrencyInstance(Locale.US).apply {
        this.currency = currency
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = fractionDigits
    }.format(abs(amountMinorUnits) / 10.0.pow(fractionDigits))
    return if (amountMinorUnits < 0) "-$amount" else "+$amount"
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasTransactions) {
                "No matching transactions"
            } else {
                "No transactions yet"
            },
            color = ReceiptAISecondaryText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransactionHistoryScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
        TransactionHistoryScreen()
    }
}
