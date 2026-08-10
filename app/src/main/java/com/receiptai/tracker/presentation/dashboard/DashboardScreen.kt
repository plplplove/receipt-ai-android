package com.receiptai.tracker.presentation.dashboard

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.pow
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIBackground

private val DashboardBackground = ReceiptAIBackground
private val CardBackground = Color.White
private val BrandPurple = Color(0xFF563994)
private val SecondaryText = Color(0xFF686572)

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

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
            DashboardBottomBar(
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
                DashboardHeader()
            }
            item {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1D21)
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
                    TransactionCard(transaction = transaction)
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFE9E2F7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = BrandPurple,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BrandPurple
        )
    }
}

@Composable
private fun BalanceCard(state: DashboardState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.labelLarge,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formatMoney(state.totalBalanceMinorUnits, state.currency),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1D21)
            )
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
                    text = "Ready for your next expense",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun MonthlySpendingCard(state: DashboardState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Monthly Spending",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1D21)
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
                text = "Your spending insights will appear here",
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
                text = "Total Spent",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            Text(
                text = totalSpent,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1D21)
            )
        }
    }
}

@Composable
private fun CategoryLegend(category: CategorySpend) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(category.color))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF353239),
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1D21),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "See All",
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
private fun TransactionCard(transaction: RecentTransaction) {
    Card(
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
                    color = Color(0xFF242229)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFE9E7EB),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Text(
                text = "-${formatMoney(transaction.amountMinorUnits, transaction.currency)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF242229)
            )
        }
    }
}

@Composable
private fun TransactionIcon(category: String) {
    val (icon, background, tint) = when (category.lowercase()) {
        "food" -> Triple(Icons.Default.LocalCafe, Color(0xFFE5F7EC), Color(0xFF43835D))
        "transport" -> Triple(Icons.Default.DirectionsCar, Color(0xFFF0EBF8), BrandPurple)
        "housing" -> Triple(Icons.Default.Home, Color(0xFFE9E2F7), BrandPurple)
            else -> Triple(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFECEBF0), SecondaryText)
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptyTransactionsCard() {
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
                text = "No transactions yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tap + to add your first expense.",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun DashboardBottomBar(
    selectedDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
            .navigationBarsPadding()
    ) {
        BottomAppBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = CardBackground,
            contentColor = SecondaryText,
            tonalElevation = 4.dp
        ) {
            BottomBarItem(
                destination = DashboardDestination.HOME,
                label = "Home",
                icon = Icons.Default.Home,
                selectedDestination = selectedDestination,
                onClick = onDestinationSelected,
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                destination = DashboardDestination.HISTORY,
                label = "History",
                icon = Icons.Default.GridView,
                selectedDestination = selectedDestination,
                onClick = onDestinationSelected,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(72.dp))
            BottomBarItem(
                destination = DashboardDestination.ANALYTICS,
                label = "Analytics",
                icon = Icons.Default.Analytics,
                selectedDestination = selectedDestination,
                onClick = onDestinationSelected,
                modifier = Modifier.weight(1f)
            )
            BottomBarItem(
                destination = DashboardDestination.SETTINGS,
                label = "Settings",
                icon = Icons.Default.Settings,
                selectedDestination = selectedDestination,
                onClick = onDestinationSelected,
                modifier = Modifier.weight(1f)
            )
        }
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = BrandPurple,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        ) {
            Text(text = "+", fontSize = 30.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: DashboardDestination,
    label: String,
    icon: ImageVector,
    selectedDestination: DashboardDestination,
    onClick: (DashboardDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = destination == selectedDestination
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick(destination) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) BrandPurple else SecondaryText,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) BrandPurple else SecondaryText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun formatMoney(amountMinorUnits: Long, currencyCode: String): String {
    val currency = runCatching { Currency.getInstance(currencyCode) }
        .getOrDefault(Currency.getInstance("USD"))
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    return NumberFormat.getCurrencyInstance(Locale.US).apply {
        this.currency = currency
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = fractionDigits
    }.format(amountMinorUnits / 10.0.pow(fractionDigits))
}

private val PreviewDashboardState = DashboardState(
    isLoading = false,
    totalBalanceMinorUnits = 4_250_00L,
    monthlySpendingMinorUnits = 1_840_00L,
    categoryBreakdown = listOf(
        CategorySpend("Housing", 45, 0xFF563994),
        CategorySpend("Food", 25, 0xFFA7E8C0),
        CategorySpend("Transport", 15, 0xFFC9B9E3),
        CategorySpend("Utilities", 15, 0xFF6C627D)
    ),
    recentTransactions = listOf(
        RecentTransaction("1", "Starbucks", 550, "USD", "Food"),
        RecentTransaction("2", "Uber", 1_200, "USD", "Transport"),
        RecentTransaction("3", "Rent", 120_000, "USD", "Housing")
    )
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DashboardScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
        DashboardScreen(
            state = PreviewDashboardState,
            onIntent = {}
        )
    }
}
