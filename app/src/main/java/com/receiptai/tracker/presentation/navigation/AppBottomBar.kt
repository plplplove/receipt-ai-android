package com.receiptai.tracker.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText

@Composable
fun ReceiptAIBottomBar(
    selectedDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = ReceiptAISurface,
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                BottomAppBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    containerColor = ReceiptAISurface,
                    contentColor = ReceiptAISecondaryText,
                    tonalElevation = 0.dp,
                    windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
                ) {
                    NavigationItem(
                        destination = DashboardDestination.HOME,
                        label = "Home",
                        icon = Icons.Default.Home,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    NavigationItem(
                        destination = DashboardDestination.HISTORY,
                        label = "History",
                        icon = Icons.Default.GridView,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(72.dp))
                    NavigationItem(
                        destination = DashboardDestination.ANALYTICS,
                        label = "Analytics",
                        icon = Icons.Default.Analytics,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    NavigationItem(
                        destination = DashboardDestination.SETTINGS,
                        label = "Settings",
                        icon = Icons.Default.Settings,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = ReceiptAIDeepPurple,
            contentColor = ReceiptAISurface,
            shape = CircleShape,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add expense",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavigationItem(
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
            .clickable { onClick(destination) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ReceiptAIDeepPurple else ReceiptAISecondaryText,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) ReceiptAIDeepPurple else ReceiptAISecondaryText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
