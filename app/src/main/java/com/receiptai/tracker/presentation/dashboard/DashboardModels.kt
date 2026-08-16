package com.receiptai.tracker.presentation.dashboard

import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.presentation.expense.AddEditTransactionUiState

enum class DashboardDestination {
    HOME,
    HISTORY,
    ANALYTICS,
    SETTINGS
}

enum class TransactionFlowScreen {
    NONE,
    DETAILS,
    ADD,
    EDIT
}

data class CategorySpend(
    val name: String,
    val percentage: Int,
    val color: Long
)

data class RecentTransaction(
    val id: String,
    val merchantName: String,
    val amountMinorUnits: Long,
    val currency: String,
    val category: String,
    val originalAmountMinorUnits: Long = amountMinorUnits,
    val originalCurrency: String = currency
)

data class DashboardState(
    val isLoading: Boolean = true,
    val totalBalanceMinorUnits: Long = 0L,
    val monthlySpendingMinorUnits: Long = 0L,
    val monthlyTransactionCount: Int = 0,
    val currency: String = "USD",
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val recentTransactions: List<RecentTransaction> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val selectedDestination: DashboardDestination = DashboardDestination.HOME,
    val transactionFlowScreen: TransactionFlowScreen = TransactionFlowScreen.NONE,
    val selectedTransactionId: String? = null,
    val transactionForm: AddEditTransactionUiState = AddEditTransactionUiState(),
    val transactionFormInitial: AddEditTransactionUiState = transactionForm,
    val isAddExpenseSheetVisible: Boolean = false,
    val isScanningReceipt: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedTransaction: Expense?
        get() = selectedTransactionId?.let { id -> expenses.firstOrNull { it.id == id } }
}

sealed interface DashboardIntent {
    data object AddExpenseClicked : DashboardIntent
    data object AddExpenseDismissed : DashboardIntent
    data object AddExpenseManuallyClicked : DashboardIntent
    data class ReceiptCaptured(val imageBytes: ByteArray) : DashboardIntent
    data class TransactionSelected(val transactionId: String) : DashboardIntent
    data object TransactionDetailsDismissed : DashboardIntent
    data object EditTransactionClicked : DashboardIntent
    data class TransactionFormChanged(val form: AddEditTransactionUiState) : DashboardIntent
    data object SaveTransactionClicked : DashboardIntent
    data object AddEditTransactionDismissed : DashboardIntent
    data object DeleteTransactionConfirmed : DashboardIntent
    data object DeleteAllDataConfirmed : DashboardIntent
    data class DisplayCurrencyChanged(val currencyCode: String) : DashboardIntent
    data class DestinationSelected(val destination: DashboardDestination) : DashboardIntent
    data object ErrorDismissed : DashboardIntent
}
