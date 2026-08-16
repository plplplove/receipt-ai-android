package com.receiptai.tracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptai.tracker.domain.model.Expense
import com.receiptai.tracker.domain.money.CurrencyConverter
import com.receiptai.tracker.domain.repository.ExpenseRepository
import com.receiptai.tracker.presentation.components.categoryAccentArgb
import com.receiptai.tracker.presentation.components.parsePositiveAmount
import com.receiptai.tracker.presentation.expense.AddEditTransactionUiState
import com.receiptai.tracker.presentation.expense.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            DashboardIntent.AddExpenseClicked -> showAddExpenseSheet()
            DashboardIntent.AddExpenseDismissed -> dismissAddExpenseSheet()
            DashboardIntent.ScanReceiptClicked -> showScanReceiptUnavailableMessage()
            DashboardIntent.AddExpenseManuallyClicked -> startAddingTransaction()
            is DashboardIntent.TransactionSelected -> showTransactionDetails(intent.transactionId)
            DashboardIntent.TransactionDetailsDismissed -> closeTransactionFlow()
            DashboardIntent.EditTransactionClicked -> startEditingTransaction()
            is DashboardIntent.TransactionFormChanged -> updateTransactionForm(intent.form)
            DashboardIntent.SaveTransactionClicked -> saveCurrentTransaction()
            DashboardIntent.AddEditTransactionDismissed -> dismissTransactionEditor()
            DashboardIntent.DeleteTransactionConfirmed -> deleteSelectedTransaction()
            DashboardIntent.DeleteAllDataConfirmed -> deleteAllData()
            is DashboardIntent.DisplayCurrencyChanged -> changeDisplayCurrency(intent.currencyCode)
            is DashboardIntent.DestinationSelected -> selectDestination(intent.destination)
            DashboardIntent.ErrorDismissed -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun showAddExpenseSheet() {
        _state.update { it.copy(isAddExpenseSheetVisible = true, errorMessage = null) }
    }

    private fun dismissAddExpenseSheet() {
        _state.update { it.copy(isAddExpenseSheetVisible = false) }
    }

    private fun showScanReceiptUnavailableMessage() {
        _state.update {
            it.copy(
                isAddExpenseSheetVisible = false,
                errorMessage = "Receipt scanning is coming soon."
            )
        }
    }

    private fun startAddingTransaction() {
        _state.update {
            it.copy(
                isAddExpenseSheetVisible = false,
                transactionFlowScreen = TransactionFlowScreen.ADD,
                selectedTransactionId = null,
                transactionForm = AddEditTransactionUiState(),
                errorMessage = null
            )
        }
    }

    private fun showTransactionDetails(transactionId: String) {
        _state.update { current ->
            if (current.expenses.none { it.id == transactionId }) {
                current.copy(errorMessage = "Transaction not found")
            } else {
                current.copy(
                    transactionFlowScreen = TransactionFlowScreen.DETAILS,
                    selectedTransactionId = transactionId,
                    errorMessage = null
                )
            }
        }
    }

    private fun startEditingTransaction() {
        _state.update { current ->
            val transaction = current.selectedTransaction ?: return@update current
            current.copy(
                transactionFlowScreen = TransactionFlowScreen.EDIT,
                transactionForm = transaction.toFormState(),
                errorMessage = null
            )
        }
    }

    private fun updateTransactionForm(form: AddEditTransactionUiState) {
        _state.update { it.copy(transactionForm = form) }
    }

    private fun changeDisplayCurrency(currencyCode: String) {
        val normalizedCurrency = currencyCode.uppercase(Locale.US)
        _state.update { currentState ->
            if (currentState.currency == normalizedCurrency || currentState.expenses.isEmpty()) {
                currentState.copy(currency = normalizedCurrency)
            } else {
                currentState.fromExpenses(
                    expenses = currentState.expenses,
                    displayCurrency = normalizedCurrency
                )
            }
        }
    }

    private fun dismissTransactionEditor() {
        _state.update { current ->
            val destination = if (
                current.transactionFlowScreen == TransactionFlowScreen.EDIT &&
                current.selectedTransaction != null
            ) {
                TransactionFlowScreen.DETAILS
            } else {
                TransactionFlowScreen.NONE
            }
            current.copy(
                transactionFlowScreen = destination,
                transactionForm = AddEditTransactionUiState(),
                isSaving = false,
                errorMessage = null
            )
        }
    }

    private fun closeTransactionFlow() {
        _state.update {
            it.copy(
                transactionFlowScreen = TransactionFlowScreen.NONE,
                selectedTransactionId = null,
                transactionForm = AddEditTransactionUiState(),
                isSaving = false,
                errorMessage = null
            )
        }
    }

    private fun selectDestination(destination: DashboardDestination) {
        _state.update {
            it.copy(
                selectedDestination = destination,
                transactionFlowScreen = TransactionFlowScreen.NONE,
                selectedTransactionId = null,
                isAddExpenseSheetVisible = false
            )
        }
    }

    private fun saveCurrentTransaction() {
        val current = _state.value
        if (current.isSaving) return

        val transactionId = when (current.transactionFlowScreen) {
            TransactionFlowScreen.EDIT -> current.selectedTransactionId
            TransactionFlowScreen.ADD -> UUID.randomUUID().toString()
            else -> null
        }
        val expense = transactionId?.let { current.transactionForm.toExpenseOrNull(it) }
        if (expense == null) {
            _state.update { it.copy(errorMessage = "Please complete all required fields.") }
            return
        }

        val wasEditing = current.transactionFlowScreen == TransactionFlowScreen.EDIT
        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { expenseRepository.saveExpense(expense) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            transactionFlowScreen = if (wasEditing) {
                                TransactionFlowScreen.DETAILS
                            } else {
                                TransactionFlowScreen.NONE
                            },
                            selectedTransactionId = if (wasEditing) expense.id else null,
                            transactionForm = AddEditTransactionUiState(),
                            isSaving = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Unable to save transaction"
                        )
                    }
                }
        }
    }

    private fun deleteSelectedTransaction() {
        val transactionId = _state.value.selectedTransactionId ?: return
        if (_state.value.isSaving) return

        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { expenseRepository.deleteExpense(transactionId) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            transactionFlowScreen = TransactionFlowScreen.NONE,
                            selectedTransactionId = null,
                            transactionForm = AddEditTransactionUiState(),
                            isSaving = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Unable to delete transaction"
                        )
                    }
                }
        }
    }

    private fun deleteAllData() {
        if (_state.value.isSaving) return

        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { expenseRepository.deleteAllExpenses() }
                .onSuccess {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Unable to delete data"
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
                _state.update { currentState -> currentState.fromExpenses(expenses) }
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

private fun AddEditTransactionUiState.toExpenseOrNull(id: String): Expense? = runCatching {
    val amount = parsePositiveAmount(totalAmount) ?: return@runCatching null
    val currency = Currency.getInstance(currencyCode)
    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val amountMinorUnits = amount
        .movePointRight(fractionDigits)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
        .takeIf { it > 0L }
        ?: return@runCatching null
    val timestamp = dateMillis ?: return@runCatching null
    val merchant = merchantName.trim().takeIf { it.isNotEmpty() } ?: return@runCatching null
    val selectedCategory = category.trim().takeIf { it.isNotEmpty() } ?: return@runCatching null

    Expense(
        id = id,
        merchantName = merchant,
        amountMinorUnits = signedAmountMinorUnits(amountMinorUnits, transactionType),
        currency = currency.currencyCode,
        dateTimestamp = timestamp,
        category = selectedCategory,
        notes = notes.trim()
    )
}.getOrNull()

private fun Expense.toFormState(): AddEditTransactionUiState {
    val currencyData = runCatching { Currency.getInstance(currency) }
        .getOrDefault(Currency.getInstance("USD"))
    val fractionDigits = currencyData.defaultFractionDigits.coerceAtLeast(0)
    val amount = abs(amountMinorUnits)
        .div(10.0.pow(fractionDigits))
        .let { value ->
            if (fractionDigits == 0) {
                value.toLong().toString()
            } else {
                String.format(Locale.US, "%.${fractionDigits}f", value)
            }
        }
    return AddEditTransactionUiState(
        merchantName = merchantName,
        totalAmount = amount,
        dateText = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            .format(dateTimestamp),
        dateMillis = dateTimestamp,
        currencyCode = currency,
        category = category,
        notes = notes,
        transactionType = if (amountMinorUnits > 0L) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }
    )
}

internal fun signedAmountMinorUnits(
    absoluteAmountMinorUnits: Long,
    transactionType: TransactionType
): Long = if (transactionType == TransactionType.INCOME) {
    absoluteAmountMinorUnits
} else {
    -absoluteAmountMinorUnits
}

private fun DashboardState.fromExpenses(
    expenses: List<Expense>,
    displayCurrency: String = currency
): DashboardState {
    val currentMonth = Calendar.getInstance().let { calendar ->
        calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
    }
    val currentMonthExpenses = expenses.filter { expense ->
        Calendar.getInstance().apply { timeInMillis = expense.dateTimestamp }.let { calendar ->
            calendar.get(Calendar.YEAR) == currentMonth.first &&
                calendar.get(Calendar.MONTH) == currentMonth.second
        }
    }
    val currentMonthSpending = currentMonthExpenses.filter { it.amountMinorUnits < 0L }
    val totalSpent = currentMonthSpending.sumOf {
        abs(it.convertedAmountMinorUnits(displayCurrency))
    }
    val categoryTotals = currentMonthSpending.groupingBy { it.category }
        .fold(0L) { total, expense ->
            total + abs(expense.convertedAmountMinorUnits(displayCurrency))
        }
    val sortedCategoryTotals = categoryTotals.entries
        .sortedByDescending { it.value }
    val categoryPercentages = calculateCategoryPercentages(
        sortedCategoryTotals.map { it.value }
    )
    val categories = sortedCategoryTotals
        .mapIndexed { index, (category, _) ->
            CategorySpend(
                name = category,
                percentage = categoryPercentages[index],
                color = categoryAccentArgb(category)
            )
        }
    val selectedStillExists = selectedTransactionId == null ||
        expenses.any { it.id == selectedTransactionId }

    return copy(
        isLoading = false,
        expenses = expenses,
        totalBalanceMinorUnits = expenses.sumOf {
            it.convertedAmountMinorUnits(displayCurrency)
        },
        monthlySpendingMinorUnits = totalSpent,
        monthlyTransactionCount = currentMonthSpending.size,
        currency = displayCurrency,
        categoryBreakdown = categories,
        recentTransactions = expenses.take(5).map {
            it.toRecentTransaction(displayCurrency)
        },
        selectedTransactionId = selectedTransactionId.takeIf { selectedStillExists },
        transactionFlowScreen = if (selectedStillExists) {
            transactionFlowScreen
        } else {
            TransactionFlowScreen.NONE
        }
    )
}

private fun Expense.toRecentTransaction(displayCurrency: String) = RecentTransaction(
    id = id,
    merchantName = merchantName,
    amountMinorUnits = convertedAmountMinorUnits(displayCurrency),
    currency = displayCurrency,
    category = category,
    originalAmountMinorUnits = amountMinorUnits,
    originalCurrency = currency
)

private fun Expense.convertedAmountMinorUnits(displayCurrency: String): Long =
    CurrencyConverter.convertMinorUnits(
        amountMinorUnits = amountMinorUnits,
        fromCurrency = currency,
        toCurrency = displayCurrency
    )

internal fun calculateCategoryPercentages(amounts: List<Long>): List<Int> {
    val positiveAmounts = amounts.map { it.coerceAtLeast(0L) }
    val total = positiveAmounts.sum()
    if (total == 0L) return List(amounts.size) { 0 }

    val exactShares = positiveAmounts.map { amount -> amount * 100.0 / total }
    val percentages = exactShares.map { it.toInt() }.toMutableList()
    var remainder = 100 - percentages.sum()
    val byLargestFraction = exactShares.indices
        .sortedByDescending { index -> exactShares[index] - percentages[index] }
    var cursor = 0
    while (remainder > 0) {
        percentages[byLargestFraction[cursor % byLargestFraction.size]] += 1
        remainder -= 1
        cursor += 1
    }
    positiveAmounts.forEachIndexed { index, amount ->
        if (amount > 0L && percentages[index] == 0) {
            val largestIndex = positiveAmounts.indices.maxBy { positiveAmounts[it] }
            if (percentages[largestIndex] > 1) {
                percentages[largestIndex] -= 1
            }
            percentages[index] = 1
        }
    }
    return percentages
}
