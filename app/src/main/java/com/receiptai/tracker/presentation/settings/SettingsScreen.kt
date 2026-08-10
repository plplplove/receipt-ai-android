package com.receiptai.tracker.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface

@Composable
fun SettingsScreen(
    onDestinationSelected: (DashboardDestination) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ReceiptAIBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ReceiptAIBottomBar(
                selectedDestination = DashboardDestination.SETTINGS,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppSectionHeader(title = "Settings")
            }
            item {
                EmptySettingsCard()
            }
        }
    }
}

@Composable
private fun EmptySettingsCard() {
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
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = ReceiptAIDeepPurple,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "Preferences will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
            }
        }
    }
}
