@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.receiptai.tracker.presentation.history

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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Storefront
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
import com.receiptai.tracker.ui.theme.ReceiptAIMint
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.presentation.components.ReceiptAIConfirmationDialog
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

private val DeleteRed = Color(0xFFC62828)
private val IncomeGreen = Color(0xFF2E7D52)
private val CompletedBackground = Color(0xFFDDF7ED)
private val PlaceholderBackground = Color(0xFFF3F1F5)

data class TransactionDetailsUiState(
    val id: String = "",
    val merchantName: String = "",
    val amountMinorUnits: Long = 0L,
    val currency: String = "USD",
    val dateText: String = "",
    val account: String = "",
    val category: String = "",
    val notes: String = "",
    val status: String = "Completed"
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ReceiptAIBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = "Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ReceiptAIPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReceiptAIBackground,
                    titleContentColor = ReceiptAIPrimaryText
                )
            )
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
            title = "Delete transaction?",
            message = "Are you sure you want to delete this transaction?",
            confirmLabel = "Delete",
            dismissLabel = "Cancel",
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
                    .background(ReceiptAIDeepPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transactionIcon(transaction.category),
                    contentDescription = null,
                    tint = ReceiptAISurface,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = transaction.merchantName.ifBlank { "Merchant" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatTransactionAmount(
                    amountMinorUnits = transaction.amountMinorUnits,
                    currencyCode = transaction.currency,
                    includeSign = true
                ),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = amountColor(transaction.amountMinorUnits)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = CompletedBackground,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = transaction.status,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Transaction details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailRow(
                icon = Icons.Default.CalendarToday,
                label = "Date",
                value = transaction.dateText.ifBlank { "Not specified" }
            )
            DetailRow(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Account",
                value = transaction.account.ifBlank { "Not specified" }
            )
            DetailRow(
                icon = Icons.Default.Category,
                label = "Category",
                value = transaction.category.ifBlank { "Uncategorized" }
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
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailIcon(icon = icon)
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
                text = "Notes",
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
                    text = notes.ifBlank { "No notes added." },
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
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = "Receipt",
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
                    text = "Receipt image",
                    style = MaterialTheme.typography.bodySmall,
                    color = ReceiptAISecondaryText
                )
            }
        }
    }
}

@Composable
private fun DetailIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ReceiptAIDeepPurple.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ReceiptAIDeepPurple,
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
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                Text("Delete")
            }
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReceiptAIDeepPurple,
                    contentColor = ReceiptAISurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit")
            }
        }
    }
}

private fun transactionIcon(category: String): ImageVector = when {
    category.contains("food", ignoreCase = true) -> Icons.Default.Storefront
    category.contains("transport", ignoreCase = true) -> Icons.AutoMirrored.Filled.ReceiptLong
    else -> Icons.Default.Category
}

private fun amountColor(amountMinorUnits: Long): Color = when {
    amountMinorUnits < 0L -> DeleteRed
    amountMinorUnits > 0L -> IncomeGreen
    else -> ReceiptAIPrimaryText
}

private fun formatTransactionAmount(
    amountMinorUnits: Long,
    currencyCode: String,
    includeSign: Boolean
): String {
    val currency = runCatching { Currency.getInstance(currencyCode) }
        .getOrDefault(Currency.getInstance("USD"))
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val amount = NumberFormat.getCurrencyInstance(Locale.US).apply {
        this.currency = currency
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = fractionDigits
    }.format(abs(amountMinorUnits) / 10.0.pow(fractionDigits))

    return if (!includeSign) {
        amount
    } else if (amountMinorUnits < 0L) {
        "-$amount"
    } else if (amountMinorUnits > 0L) {
        "+$amount"
    } else {
        amount
    }
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
