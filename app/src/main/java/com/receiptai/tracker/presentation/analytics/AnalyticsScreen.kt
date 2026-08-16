package com.receiptai.tracker.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.presentation.dashboard.CategorySpend
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.components.formatMoney
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIHeroGradient
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.presentation.localization.receiptAIStrings

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    monthlySpendingMinorUnits: Long = 0L,
    currency: String = "USD",
    categoryBreakdown: List<CategorySpend> = emptyList(),
    transactionCount: Int = 0,
    onDestinationSelected: (DashboardDestination) -> Unit = {},
    onAddExpenseClick: () -> Unit = {}
) {
    val strings = receiptAIStrings()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ReceiptAIBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ReceiptAIBottomBar(
                selectedDestination = DashboardDestination.ANALYTICS,
                onDestinationSelected = onDestinationSelected,
                onAddClick = onAddExpenseClick
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.lazy.LazyColumn(
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
            item { AppSectionHeader(title = strings.analytics) }
            if (categoryBreakdown.isEmpty()) {
                item { EmptyAnalyticsCard() }
            } else {
                item {
                    AnalyticsSummaryCard(
                        monthlySpendingMinorUnits = monthlySpendingMinorUnits,
                        currency = currency,
                        transactionCount = transactionCount
                    )
                }
                item {
                    CategoryBreakdownCard(categories = categoryBreakdown)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSummaryCard(
    monthlySpendingMinorUnits: Long,
    currency: String,
    transactionCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(ReceiptAIHeroGradient))
        ) {
            SummaryCardOrnaments()
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = receiptAIStrings().thisMonth(currency),
                    style = MaterialTheme.typography.titleMedium,
                    color = ReceiptAIPrimaryText.copy(alpha = 0.72f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatMoney(monthlySpendingMinorUnits, currency),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptAIPrimaryText
                )
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    color = ReceiptAIPrimaryText.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = ReceiptAIPrimaryText.copy(alpha = 0.78f),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = receiptAIStrings().savedTransactions(transactionCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ReceiptAIPrimaryText,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCardOrnaments() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        drawCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = size.minDimension * 0.40f,
            center = Offset(
                x = size.width * 0.10f,
                y = size.height * 0.05f
            )
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.07f),
            radius = size.minDimension * 0.28f,
            center = Offset(
                x = size.width * 0.88f,
                y = size.height * 0.70f
            )
        )
    }
}

@Composable
private fun CategoryBreakdownCard(categories: List<CategorySpend>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = receiptAIStrings().spendingByCategory,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            categories.forEach { category ->
                CategoryProgress(category = category)
            }
        }
    }
}

@Composable
private fun CategoryProgress(category: CategorySpend) {
    val strings = receiptAIStrings()
    val categoryColor = Color(category.color)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(categoryColor, CircleShape)
            )
            Text(
                text = strings.categoryLabel(category.name),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ReceiptAIPrimaryText,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Text(
                text = "${category.percentage}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = ReceiptAISecondaryText
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(ReceiptAIBackground, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((category.percentage / 100f).coerceIn(0.02f, 1f))
                    .height(8.dp)
                    .background(categoryColor, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun EmptyAnalyticsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 64.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = ReceiptAIDeepPurple
                )
                Text(
                    text = receiptAIStrings().noAnalytics,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptAIDeepPurple,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = receiptAIStrings().analyticsEmptySubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AnalyticsScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme() {
        AnalyticsScreen(
            monthlySpendingMinorUnits = 184_000,
            currency = "USD",
            categoryBreakdown = listOf(
                CategorySpend("Housing", 45, 0xFF563994),
                CategorySpend("Food & Dining", 30, 0xFF00BFA5),
                CategorySpend("Transport", 25, 0xFFC9B9E3)
            ),
            transactionCount = 12
        )
    }
}
