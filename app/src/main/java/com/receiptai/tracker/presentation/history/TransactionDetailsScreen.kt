@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.receiptai.tracker.presentation.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIOnBrand
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAIMint
import com.receiptai.tracker.presentation.components.ReceiptAIConfirmationDialog
import com.receiptai.tracker.presentation.components.categoryVisualStyle
import com.receiptai.tracker.presentation.components.formatMoney
import com.receiptai.tracker.presentation.localization.receiptAIStrings

private val DeleteRed = Color(0xFFC62828)
private val IncomeGreen = Color(0xFF2E7D52)
private val CompletedBackground: Color
    @Composable get() = ReceiptAIMint.copy(alpha = 0.14f)
private val PlaceholderBackground: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

data class TransactionDetailsUiState(
    val id: String = "",
    val merchantName: String = "",
    val amountMinorUnits: Long = 0L,
    val currency: String = "USD",
    val dateText: String = "",
    val account: String = "",
    val category: String = "",
    val notes: String = "",
    val status: String = "Completed",
    val originalAmountMinorUnits: Long = amountMinorUnits,
    val originalCurrency: String = currency
)

@Composable
fun TransactionDetailsScreen(
    transaction: TransactionDetailsUiState,
    onBack: () -> Unit,
    onDelete: (TransactionDetailsUiState) -> Unit,
    onEdit: (TransactionDetailsUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    val strings = receiptAIStrings()

    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ReceiptAIBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ReceiptAIBackground)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.details,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.details,
                                tint = ReceiptAIPrimaryText
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ReceiptAIBackground,
                        titleContentColor = ReceiptAIPrimaryText
                    )
                )
            }
        },
        bottomBar = {
            TransactionDetailsActions(
                onDeleteClick = { isDeleteDialogVisible = true },
                onEditClick = { onEdit(transaction) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TransactionSummaryCard(transaction = transaction)
            }
            item {
                TransactionInformationCard(transaction = transaction)
            }
        }
    }

    if (isDeleteDialogVisible) {
        ReceiptAIConfirmationDialog(
            title = strings.deleteTransactionTitle,
            message = strings.deleteTransactionMessage,
            confirmLabel = strings.delete,
            dismissLabel = strings.cancel,
            icon = Icons.Default.DeleteOutline,
            confirmColor = DeleteRed,
            onConfirm = {
                isDeleteDialogVisible = false
                onDelete(transaction)
            },
            onDismiss = { isDeleteDialogVisible = false }
        )
    }
}

@Composable
private fun TransactionSummaryCard(transaction: TransactionDetailsUiState) {
    val strings = receiptAIStrings()
    val categoryStyle = categoryVisualStyle(transaction.category)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(categoryStyle.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryStyle.icon,
                    contentDescription = null,
                    tint = categoryStyle.accent,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = transaction.merchantName.ifBlank { strings.merchant },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatMoney(
                    amountMinorUnits = transaction.amountMinorUnits,
                    currencyCode = transaction.currency,
                    includePositiveSign = true
                ),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = amountColor(transaction.amountMinorUnits)
            )
            if (transaction.originalCurrency != transaction.currency) {
                Text(
                    text = strings.original(
                        formatMoney(
                            transaction.originalAmountMinorUnits,
                            transaction.originalCurrency,
                            includePositiveSign = true
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText.copy(alpha = 0.72f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = CompletedBackground,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = strings.completed,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = IncomeGreen,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TransactionInformationCard(transaction: TransactionDetailsUiState) {
    val strings = receiptAIStrings()
    val categoryStyle = categoryVisualStyle(transaction.category)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = strings.transactionDetails,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailRow(
                icon = Icons.Default.CalendarToday,
                label = strings.date,
                value = transaction.dateText.ifBlank { strings.notSpecified }
            )
            DetailRow(
                icon = Icons.Default.AccountBalanceWallet,
                label = strings.account,
                value = transaction.account.ifBlank { strings.notSpecified }
            )
            DetailRow(
                icon = categoryStyle.icon,
                label = strings.category,
                value = transaction.category
                    .takeIf { it.isNotBlank() }
                    ?.let(strings.categoryLabel)
                    ?: strings.uncategorized,
                iconTint = categoryStyle.accent,
                iconContainerColor = categoryStyle.container
            )
            NotesRow(notes = transaction.notes)
            ReceiptPlaceholder()
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = ReceiptAIDeepPurple,
    iconContainerColor: Color = ReceiptAIDeepPurple.copy(alpha = 0.10f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailIcon(
            icon = icon,
            tint = iconTint,
            containerColor = iconContainerColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = ReceiptAISecondaryText,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = ReceiptAIPrimaryText
        )
    }
}

@Composable
private fun NotesRow(notes: String) {
    val strings = receiptAIStrings()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        DetailIcon(icon = Icons.Default.Description)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = strings.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = ReceiptAISecondaryText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = PlaceholderBackground,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notes.ifBlank { receiptAIStrings().noNotesAdded },
                    style = MaterialTheme.typography.bodySmall,
                    color = ReceiptAISecondaryText,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ReceiptPlaceholder() {
    val strings = receiptAIStrings()
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = receiptAIStrings().receipt,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = ReceiptAIPrimaryText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PlaceholderBackground)
                .border(
                    width = 1.dp,
                    color = ReceiptAISecondaryText.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = ReceiptAIDeepPurple,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = receiptAIStrings().receiptImage,
                    style = MaterialTheme.typography.bodySmall,
                    color = ReceiptAISecondaryText
                )
            }
        }
    }
}

@Composable
private fun DetailIcon(
    icon: ImageVector,
    tint: Color = ReceiptAIDeepPurple,
    containerColor: Color = ReceiptAIDeepPurple.copy(alpha = 0.10f)
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun TransactionDetailsActions(
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Surface(
        color = ReceiptAIBackground,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DeleteRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeleteRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(receiptAIStrings().delete)
            }
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReceiptAIDeepPurple,
                    contentColor = ReceiptAIOnBrand
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(receiptAIStrings().edit)
            }
        }
    }
}

@Composable
private fun amountColor(amountMinorUnits: Long): Color = when {
    amountMinorUnits < 0L -> DeleteRed
    amountMinorUnits > 0L -> IncomeGreen
    else -> ReceiptAIPrimaryText
}

private val PreviewTransactionDetails = TransactionDetailsUiState(
    id = "preview-transaction",
    merchantName = "Sweetgreen",
    amountMinorUnits = -1_450L,
    currency = "USD",
    dateText = "Aug 10, 2026 · 12:45 PM",
    account = "Main account",
    category = "Food & Dining",
    notes = "Lunch with the team.",
    status = "Completed"
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransactionDetailsScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
        TransactionDetailsScreen(
            transaction = PreviewTransactionDetails,
            onBack = {},
            onDelete = {},
            onEdit = {}
        )
    }
}
