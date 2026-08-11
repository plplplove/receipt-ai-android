package com.receiptai.tracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.domain.repository.ExpenseRepository
import com.receiptai.tracker.presentation.expense.AddEditTransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.RoundingMode
import java.util.Currency
import java.util.UUID
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlin.math.abs

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    private var observationJob: Job? = null

    init {
        observeExpenses()
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Refresh -> observeExpenses()
            DashboardIntent.AddExpenseClicked -> {
                _state.update { it.copy(isAddExpenseSheetVisible = true) }
            }
            DashboardIntent.AddExpenseDismissed -> {
                _state.update { it.copy(isAddExpenseSheetVisible = false) }
            }
            DashboardIntent.AddExpenseManuallyClicked -> {
                _state.update {
                    it.copy(
                        isAddExpenseSheetVisible = false,
                        isAddEditTransactionVisible = true
                    )
                }
            }
            DashboardIntent.AddEditTransactionDismissed -> {
                _state.update { it.copy(isAddEditTransactionVisible = false) }
            }
            is DashboardIntent.DestinationSelected -> {
                _state.update { it.copy(selectedDestination = intent.destination) }
            }
            DashboardIntent.ErrorDismissed -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun saveTransaction(formState: AddEditTransactionUiState) {
        val expense = formState.toExpenseOrNull()
        if (expense == null) {
            _state.update {
                it.copy(errorMessage = "Please complete all required fields.")
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                expenseRepository.saveExpense(expense)
            }.onSuccess {
                _state.update {
                    it.copy(
                        isAddEditTransactionVisible = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Unable to save the transaction"
                    )
                }
            }
        }
    }

    private fun observeExpenses() {
        observationJob?.cancel()
        observationJob = expenseRepository.observeExpenses()
            .onStart {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
            }
            .onEach { expenses ->
                _state.update { currentState ->
                    currentState.fromExpenses(expenses)
                }
            }
            .catch { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load expenses"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

private fun AddEditTransactionUiState.toExpenseOrNull(): Expense? = runCatching {
    val amount = totalAmount.toBigDecimalOrNull() ?: return@runCatching null
    val currency = Currency.getInstance(currencyCode)
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val amountMinorUnits = amount
        .movePointRight(fractionDigits)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
        .takeIf { it > 0L }
        ?: return@runCatching null
    val timestamp = dateMillis ?: return@runCatching null

    Expense(
        id = UUID.randomUUID().toString(),
        merchantName = merchantName.trim(),
        amountMinorUnits = -amountMinorUnits,
        currency = currencyCode,
        dateTimestamp = timestamp,
        category = category.trim()
    )
}.getOrNull()

private fun DashboardState.fromExpenses(expenses: List<Expense>): DashboardState {
    val currentMonth = Calendar.getInstance().let { calendar ->
        calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
    }
    val currentMonthExpenses = expenses.filter { expense ->
        Calendar.getInstance().apply { timeInMillis = expense.dateTimestamp }.let { calendar ->
            calendar.get(Calendar.YEAR) == currentMonth.first &&
                calendar.get(Calendar.MONTH) == currentMonth.second
        }
    }
    val totalSpent = currentMonthExpenses
        .filter { it.amountMinorUnits < 0L }
        .sumOf { abs(it.amountMinorUnits) }
    val categoryTotals = currentMonthExpenses.groupingBy { it.category }
        .fold(0L) { total, expense -> total + abs(expense.amountMinorUnits) }
    val categoryTotal = categoryTotals.values.sum().takeIf { it > 0L } ?: 1L
    val categories = categoryTotals.entries
        .sortedByDescending { it.value }
        .map { (category, amount) ->
            CategorySpend(
                name = category,
                percentage = ((amount * 100) / categoryTotal).toInt().coerceAtLeast(1),
                color = categoryColor(category)
            )
        }

    return copy(
        isLoading = false,
        expenses = expenses,
        totalBalanceMinorUnits = expenses.sumOf { it.amountMinorUnits },
        monthlySpendingMinorUnits = totalSpent,
        currency = currentMonthExpenses.firstOrNull()?.currency ?: currency,
        categoryBreakdown = categories,
        recentTransactions = expenses.take(5).map { it.toRecentTransaction() },
        errorMessage = null
    )
}

private fun Expense.toRecentTransaction() = RecentTransaction(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = amountMinorUnits,
    currency = currency,
    category = category
)

private fun categoryColor(category: String): Long = when (category.lowercase()) {
    "housing" -> 0xFF563994
    "food" -> 0xFFA7E8C0
    "transport" -> 0xFFC9B9E3
    "utilities" -> 0xFF6C627D
    else -> 0xFF8D7AA8
}
