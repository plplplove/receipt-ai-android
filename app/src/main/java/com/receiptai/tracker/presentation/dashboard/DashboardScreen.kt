package com.receiptai.tracker.presentation.dashboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.domain.money.CurrencyConverter
import com.receiptai.tracker.presentation.analytics.AnalyticsScreen
import com.receiptai.tracker.presentation.components.categoryVisualStyle
import com.receiptai.tracker.presentation.components.formatMoney
import com.receiptai.tracker.presentation.expense.AddExpenseBottomSheet
import com.receiptai.tracker.presentation.expense.AddEditTransactionMode
import com.receiptai.tracker.presentation.expense.AddEditTransactionScreen
import com.receiptai.tracker.presentation.history.HistoryTransaction
import com.receiptai.tracker.presentation.history.TransactionDetailsScreen
import com.receiptai.tracker.presentation.history.TransactionDetailsUiState
import com.receiptai.tracker.presentation.history.TransactionHistoryScreen
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.presentation.settings.SettingsScreen
import com.receiptai.tracker.presentation.settings.writeReceiptAiCsv
import com.receiptai.tracker.presentation.localization.AppLanguage
import com.receiptai.tracker.presentation.localization.localizedError
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ThemeMode

private val DashboardBackground: Color
    @Composable get() = ReceiptAIBackground
private val CardBackground: Color
    @Composable get() = ReceiptAISurface
private val BrandPurple = ReceiptAIDeepPurple
private val SecondaryText: Color
    @Composable get() = ReceiptAISecondaryText

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.SYSTEM_DEFAULT,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    displayCurrency: String = "USD",
    onDisplayCurrencyChanged: (String) -> Unit = {},
    appLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageChanged: (AppLanguage) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val strings = receiptAIStrings()
    val expensesForExport by rememberUpdatedState(state.expenses)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { selectedUri ->
            writeReceiptAiCsv(context, selectedUri, expensesForExport)
                .onSuccess {
                    Toast.makeText(
                        context,
                        strings.csvExported,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure {
                    Toast.makeText(
                        context,
                        strings.csvExportFailed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    LaunchedEffect(displayCurrency) {
        viewModel.onIntent(DashboardIntent.DisplayCurrencyChanged(displayCurrency))
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(strings.localizedError(message))
            viewModel.onIntent(DashboardIntent.ErrorDismissed)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (state.transactionFlowScreen) {
            TransactionFlowScreen.DETAILS -> {
                state.selectedTransaction?.let { transaction ->
                    TransactionDetailsScreen(
                        transaction = transaction.toDetailsUiState(state.currency),
                        onBack = {
                            viewModel.onIntent(DashboardIntent.TransactionDetailsDismissed)
                        },
                        onDelete = {
                            viewModel.onIntent(DashboardIntent.DeleteTransactionConfirmed)
                        },
                        onEdit = {
                            viewModel.onIntent(DashboardIntent.EditTransactionClicked)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            TransactionFlowScreen.ADD,
            TransactionFlowScreen.EDIT -> {
                AddEditTransactionScreen(
                    state = state.transactionForm,
                    mode = if (state.transactionFlowScreen == TransactionFlowScreen.EDIT) {
                        AddEditTransactionMode.EDIT_EXPENSE
                    } else {
                        AddEditTransactionMode.ADD_EXPENSE
                    },
                    onBack = {
                        viewModel.onIntent(DashboardIntent.AddEditTransactionDismissed)
                    },
                    onStateChange = { form ->
                        viewModel.onIntent(DashboardIntent.TransactionFormChanged(form))
                    },
                    onConfirmSave = {
                        viewModel.onIntent(DashboardIntent.SaveTransactionClicked)
                    },
                    isSaving = state.isSaving,
                    modifier = Modifier.fillMaxSize()
                )
            }
            TransactionFlowScreen.NONE -> when (state.selectedDestination) {
                DashboardDestination.HISTORY -> TransactionHistoryScreen(
                    transactions = state.expenses.map {
                        it.toHistoryTransaction(state.currency)
                    },
                    onDestinationSelected = { destination ->
                        viewModel.onIntent(DashboardIntent.DestinationSelected(destination))
                    },
                    onAddExpenseClick = {
                        viewModel.onIntent(DashboardIntent.AddExpenseClicked)
                    },
                    onTransactionClick = { transaction ->
                        viewModel.onIntent(DashboardIntent.TransactionSelected(transaction.id))
                    },
                    modifier = Modifier.fillMaxSize()
                )
                DashboardDestination.ANALYTICS -> AnalyticsScreen(
                    monthlySpendingMinorUnits = state.monthlySpendingMinorUnits,
                    currency = state.currency,
                    categoryBreakdown = state.categoryBreakdown,
                    transactionCount = state.monthlyTransactionCount,
                    onDestinationSelected = { destination ->
                        viewModel.onIntent(DashboardIntent.DestinationSelected(destination))
                    },
                    onAddExpenseClick = {
                        viewModel.onIntent(DashboardIntent.AddExpenseClicked)
                    },
                    modifier = Modifier.fillMaxSize()
                )
                DashboardDestination.SETTINGS -> SettingsScreen(
                    themeMode = themeMode,
                    onThemeModeSelected = onThemeModeChanged,
                    currencyCode = displayCurrency,
                    onCurrencySelected = { currencyCode ->
                        onDisplayCurrencyChanged(currencyCode)
                    },
                    appLanguage = appLanguage,
                    onLanguageSelected = onLanguageChanged,
                    onDestinationSelected = { destination ->
                        viewModel.onIntent(DashboardIntent.DestinationSelected(destination))
                    },
                    onAddExpenseClick = {
                        viewModel.onIntent(DashboardIntent.AddExpenseClicked)
                    },
                    onDeleteAllData = {
                        viewModel.onIntent(DashboardIntent.DeleteAllDataConfirmed)
                    },
                    onExportData = {
                        exportLauncher.launch("receiptai_expenses.csv")
                    },
                    modifier = Modifier.fillMaxSize()
                )
                else -> DashboardScreen(
                    state = state,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (
            state.transactionFlowScreen == TransactionFlowScreen.NONE &&
            state.isAddExpenseSheetVisible
        ) {
            AddExpenseBottomSheet(
                onDismissRequest = {
                    viewModel.onIntent(DashboardIntent.AddExpenseDismissed)
                },
                onScanReceipt = {
                    viewModel.onIntent(DashboardIntent.ScanReceiptClicked)
                },
                onAddManually = {
                    viewModel.onIntent(DashboardIntent.AddExpenseManuallyClicked)
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        )
    }
}

private fun Expense.toHistoryTransaction(displayCurrency: String) = HistoryTransaction(
    id = id,
    dateGroup = historyDateGroup(dateTimestamp),
    merchantName = merchantName,
    category = category,
    amountMinorUnits = CurrencyConverter.convertMinorUnits(
        amountMinorUnits,
        currency,
        displayCurrency
    ),
    currency = displayCurrency,
    originalAmountMinorUnits = amountMinorUnits,
    originalCurrency = currency
)

private fun Expense.toDetailsUiState(displayCurrency: String) = TransactionDetailsUiState(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = CurrencyConverter.convertMinorUnits(
        amountMinorUnits,
        currency,
        displayCurrency
    ),
    currency = displayCurrency,
    dateText = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(dateTimestamp),
    account = "Main account",
    category = category,
    notes = notes,
    status = "Completed",
    originalAmountMinorUnits = amountMinorUnits,
    originalCurrency = currency
)

private fun historyDateGroup(timestamp: Long): String {
    val today = Calendar.getInstance().startOfDay()
    val transactionDay = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }.startOfDay()

    if (!transactionDay.before(today)) return "Today"
    val candidate = today.clone() as Calendar
    for (daysAgo in 1..6) {
        candidate.add(Calendar.DAY_OF_YEAR, -1)
        if (candidate.isSameDay(transactionDay)) {
            return if (daysAgo == 1) "Yesterday" else "This Week"
        }
    }
    return "Older"
}

private fun Calendar.startOfDay(): Calendar = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.isSameDay(other: Calendar): Boolean =
    get(Calendar.ERA) == other.get(Calendar.ERA) &&
        get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
        get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

@Composable
fun DashboardScreen(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DashboardBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ReceiptAIBottomBar(
                selectedDestination = state.selectedDestination,
                onDestinationSelected = { destination ->
                    onIntent(DashboardIntent.DestinationSelected(destination))
                },
                onAddClick = { onIntent(DashboardIntent.AddExpenseClicked) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppSectionHeader(title = receiptAIStrings().home)
            }
            item {
                Text(
                    text = receiptAIStrings().welcomeBack,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptAIPrimaryText
                )
            }
            item {
                BalanceCard(state = state)
            }
            item {
                MonthlySpendingCard(state = state)
            }
            item {
                RecentTransactionsHeader(
                    onSeeAllClick = {
                        onIntent(
                            DashboardIntent.DestinationSelected(DashboardDestination.HISTORY)
                        )
                    }
                )
            }
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandPurple)
                    }
                }
            } else if (state.recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionsCard()
                }
            } else {
                items(
                    items = state.recentTransactions,
                    key = { transaction -> transaction.id }
                ) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onClick = {
                            onIntent(DashboardIntent.TransactionSelected(transaction.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(state: DashboardState) {
    val strings = receiptAIStrings()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = strings.totalBalance,
                style = MaterialTheme.typography.labelLarge,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formatMoney(state.totalBalanceMinorUnits, state.currency),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            if (state.expenses.any { it.currency != state.currency }) {
                Text(
                    text = strings.convertedTo(state.currency),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color(0xFF43835D),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = strings.readyForNextExpense,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun MonthlySpendingCard(state: DashboardState) {
    val strings = receiptAIStrings()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = strings.monthlySpending(state.currency),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state.categoryBreakdown.isEmpty()) {
                EmptySpendingSummary()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpendingDonut(
                        totalSpent = formatMoney(
                            state.monthlySpendingMinorUnits,
                            state.currency
                        ),
                        categories = state.categoryBreakdown,
                        modifier = Modifier.size(164.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.categoryBreakdown.forEach { category ->
                            CategoryLegend(category = category)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySpendingSummary() {
    val strings = receiptAIStrings()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = BrandPurple,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.spendingInsights,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun SpendingDonut(
    totalSpent: String,
    categories: List<CategorySpend>,
    modifier: Modifier = Modifier
) {
    val strings = receiptAIStrings()
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            var startAngle = -90f
            categories.forEach { category ->
                val sweepAngle = category.percentage / 100f * 360f
                drawArc(
                    color = Color(category.color),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 3f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = strings.totalSpent,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            Text(
                text = totalSpent,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
        }
    }
}

@Composable
private fun CategoryLegend(category: CategorySpend) {
    val strings = receiptAIStrings()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(category.color))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = strings.categoryLabel(category.name),
            style = MaterialTheme.typography.bodySmall,
            color = ReceiptAIPrimaryText,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${category.percentage}%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = SecondaryText
        )
    }
}

@Composable
private fun RecentTransactionsHeader(onSeeAllClick: () -> Unit) {
    val strings = receiptAIStrings()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = strings.recentTransactions,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ReceiptAIPrimaryText,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = strings.seeAll,
            style = MaterialTheme.typography.labelLarge,
            color = BrandPurple,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onSeeAllClick)
                .padding(8.dp)
        )
    }
}

@Composable
private fun TransactionCard(
    transaction: RecentTransaction,
    onClick: () -> Unit
) {
    val strings = receiptAIStrings()
    val categoryStyle = categoryVisualStyle(transaction.category)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransactionIcon(category = transaction.category)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ReceiptAIPrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = categoryStyle.container,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = strings.categoryLabel(transaction.category),
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryStyle.accent,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(
                        transaction.amountMinorUnits,
                        transaction.currency,
                        includePositiveSign = true
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (transaction.amountMinorUnits < 0L) {
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
                        color = SecondaryText.copy(alpha = 0.68f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionIcon(category: String) {
    val style = categoryVisualStyle(category)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(style.container),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.accent,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptyTransactionsCard() {
    val strings = receiptAIStrings()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = null,
                tint = BrandPurple,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.noTransactions,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = strings.tapToAddFirstExpense,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DashboardScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
        DashboardScreen(
            state = DashboardState(isLoading = false),
            onIntent = {}
        )
    }
}
