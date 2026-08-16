package com.receiptai.tracker.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIBrandViolet
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIOnBrand
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface

private val BottomBarShape = RoundedCornerShape(32.dp)
private const val PressScale = 0.86f

@Composable
fun ReceiptAIBottomBar(
    selectedDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = receiptAIStrings()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ReceiptAIBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                shape = BottomBarShape,
                color = ReceiptAISurface,
                shadowElevation = 14.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = ReceiptAIPrimaryText.copy(alpha = 0.06f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationItem(
                        destination = DashboardDestination.HOME,
                        label = strings.navigationHome,
                        icon = Icons.Default.Home,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    NavigationItem(
                        destination = DashboardDestination.HISTORY,
                        label = strings.navigationHistory,
                        icon = Icons.Default.GridView,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(72.dp))
                    NavigationItem(
                        destination = DashboardDestination.ANALYTICS,
                        label = strings.navigationAnalytics,
                        icon = Icons.Default.Analytics,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                    NavigationItem(
                        destination = DashboardDestination.SETTINGS,
                        label = strings.navigationSettings,
                        icon = Icons.Default.Settings,
                        selectedDestination = selectedDestination,
                        onClick = onDestinationSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            AddExpenseButton(
                onClick = onAddClick,
                contentDescription = strings.addExpense,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp)
            )
        }
    }
}

@Composable
private fun AddExpenseButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "addButtonScale"
    )
    Box(
        modifier = modifier
            .size(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(ReceiptAIDeepPurple, ReceiptAIBrandViolet)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription,
            tint = ReceiptAIOnBrand,
            modifier = Modifier.size(30.dp)
        )
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navItemScale"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) ReceiptAIDeepPurple else ReceiptAISecondaryText,
        animationSpec = tween(durationMillis = 150),
        label = "navItemColor"
    )
    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(destination) }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSelected) {
                        ReceiptAIDeepPurple.copy(alpha = 0.12f)
                    } else {
                        Color.Transparent
                    }
                )
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .size(21.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
