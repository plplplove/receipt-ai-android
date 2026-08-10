@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.receiptai.tracker.presentation.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIMint
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText

@Composable
fun AddExpenseBottomSheet(
    onDismissRequest: () -> Unit,
    onScanReceipt: () -> Unit,
    onAddManually: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = ReceiptAISurface,
        contentColor = ReceiptAIPrimaryText,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ReceiptAIDeepPurple
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose how you want to log your expense.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
                Spacer(modifier = Modifier.height(24.dp))
                ExpenseOptionCard(
                    title = "Scan Receipt",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    iconTint = ReceiptAIMint,
                    iconBackground = Color(0xFFD7F8E8),
                    onClick = onScanReceipt
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExpenseOptionCard(
                    title = "Add Manually",
                    icon = Icons.Default.Edit,
                    iconTint = ReceiptAIDeepPurple,
                    iconBackground = Color(0xFFEDE7F6),
                    onClick = onAddManually
                )
            }
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close add expense sheet",
                    tint = ReceiptAISecondaryText
                )
            }
        }
    }
}

@Composable
private fun ExpenseOptionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(154.dp)
            .shadow(3.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                    color = ReceiptAIDeepPurple
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AddExpenseBottomSheetPreview() {
    ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
        AddExpenseBottomSheet(
            onDismissRequest = {},
            onScanReceipt = {},
            onAddManually = {}
        )
    }
}
