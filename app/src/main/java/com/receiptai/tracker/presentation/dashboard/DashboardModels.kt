package com.receiptai.tracker.presentation.dashboard

enum class DashboardDestination {
    HOME,
    HISTORY,
    ANALYTICS,
    SETTINGS
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
    val category: String
)

data class DashboardState(
    val isLoading: Boolean = true,
    val totalBalanceMinorUnits: Long = 0L,
    val monthlySpendingMinorUnits: Long = 0L,
    val currency: String = "USD",
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val recentTransactions: List<RecentTransaction> = emptyList(),
    val selectedDestination: DashboardDestination = DashboardDestination.HOME,
    val isAddExpenseSheetVisible: Boolean = false,
    val errorMessage: String? = null
)

sealed interface DashboardIntent {
    data object Refresh : DashboardIntent
    data object AddExpenseClicked : DashboardIntent
    data object AddExpenseDismissed : DashboardIntent
    data class DestinationSelected(val destination: DashboardDestination) : DashboardIntent
    data object ErrorDismissed : DashboardIntent
}
