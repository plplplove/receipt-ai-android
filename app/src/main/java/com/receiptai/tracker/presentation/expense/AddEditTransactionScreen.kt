@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.receiptai.tracker.presentation.expense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIMint
import com.receiptai.tracker.ui.theme.ReceiptAIOnBrand
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISystemBarsEffect
import com.receiptai.tracker.presentation.components.ReceiptAIConfirmationDialog
import com.receiptai.tracker.presentation.components.parsePositiveAmount
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import com.receiptai.tracker.presentation.localization.ReceiptAIStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AddEditTransactionMode {
    ADD_EXPENSE,
    EDIT_EXPENSE
}

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class AddEditTransactionUiState(
    val merchantName: String = "",
    val totalAmount: String = "",
    val dateText: String = "",
    val dateMillis: Long? = null,
    val currencyCode: String = "",
    val category: String = "",
    val notes: String = "",
    val transactionType: TransactionType = TransactionType.EXPENSE
)

data class TransactionCurrencyOption(
    val code: String
)

private data class TransactionDropdownOption(
    val value: String,
    val label: String
)

private val DefaultTransactionCategories = listOf(
    "Food & Dining",
    "Transport",
    "Shopping",
    "Health",
    "Housing",
    "Utilities",
    "Other"
)

private val DefaultTransactionCurrencies = listOf(
    TransactionCurrencyOption("USD"),
    TransactionCurrencyOption("EUR"),
    TransactionCurrencyOption("GBP"),
    TransactionCurrencyOption("PLN"),
    TransactionCurrencyOption("CAD"),
    TransactionCurrencyOption("AUD"),
    TransactionCurrencyOption("JPY")
)

private val ValidationRed = Color(0xFFC62828)
private val IncomeGreen = Color(0xFF2E7D52)

@Composable
fun AddEditTransactionScreen(
    state: AddEditTransactionUiState,
    onBack: () -> Unit,
    onStateChange: (AddEditTransactionUiState) -> Unit,
    onConfirmSave: () -> Unit,
    modifier: Modifier = Modifier,
    mode: AddEditTransactionMode = AddEditTransactionMode.ADD_EXPENSE,
    categories: List<String> = DefaultTransactionCategories,
    currencies: List<TransactionCurrencyOption> = DefaultTransactionCurrencies,
    isSaving: Boolean = false
) {
    var isCategoryMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var isCurrencyMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var isCalendarVisible by rememberSaveable { mutableStateOf(false) }
    var isDiscardDialogVisible by rememberSaveable { mutableStateOf(false) }
    var amountHasBeenEdited by rememberSaveable { mutableStateOf(state.totalAmount.isNotBlank()) }
    val initialState = androidx.compose.runtime.remember { state }
    val strings = receiptAIStrings()
    val title = when (mode) {
        AddEditTransactionMode.ADD_EXPENSE -> strings.addExpense
        AddEditTransactionMode.EDIT_EXPENSE -> strings.editExpense
    }
    val secondaryActionLabel = when (mode) {
        AddEditTransactionMode.ADD_EXPENSE -> strings.cancel
        AddEditTransactionMode.EDIT_EXPENSE -> strings.cancel
    }
    val isFormValid = state.isFormValid()
    val hasAmountError = amountHasBeenEdited && !state.isAmountValid()
    val missingRequiredFields = state.missingRequiredFields(strings)
    val validationMessage = when {
        hasAmountError -> {
            strings.enterPositiveAmount
        }
        missingRequiredFields.isNotEmpty() -> {
            strings.requiredFields(missingRequiredFields.joinToString())
        }
        else -> null
    }
    val requestExit = {
        if (state != initialState) {
            isDiscardDialogVisible = true
        } else {
            onBack()
        }
    }

    BackHandler(onBack = requestExit)

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
                            text = title,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = requestExit) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.navigateBack,
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
            AddEditActions(
                secondaryLabel = secondaryActionLabel,
                isFormValid = isFormValid,
                isSaving = isSaving,
                validationMessage = validationMessage,
                hasAmountError = hasAmountError,
                    onConfirmClick = { onConfirmSave() },
                onSecondaryClick = requestExit
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TransactionFormCard(
                    state = state,
                    categories = categories,
                    currencies = currencies,
                    isCategoryMenuExpanded = isCategoryMenuExpanded,
                    isCurrencyMenuExpanded = isCurrencyMenuExpanded,
                    onCategoryMenuExpandedChange = {
                        isCategoryMenuExpanded = it
                    },
                    onCurrencyMenuExpandedChange = {
                        isCurrencyMenuExpanded = it
                    },
                    onDateClick = {
                        isCalendarVisible = true
                    },
                    categoryLeadingIcon = Icons.Default.Category,
                    amountHasBeenEdited = amountHasBeenEdited,
                    onAmountEdited = { amountHasBeenEdited = true },
                    onStateChange = onStateChange
                )
            }
        }
    }

    if (isCalendarVisible) {
        CustomCalendarDialog(
            initialDateMillis = state.dateMillis ?: System.currentTimeMillis(),
            onDismissRequest = { isCalendarVisible = false },
            onDateSelected = { selectedDateMillis ->
                onStateChange(
                    state.copy(
                        dateMillis = selectedDateMillis,
                        dateText = formatDate(selectedDateMillis)
                    )
                )
                isCalendarVisible = false
            }
        )
    }

    if (isDiscardDialogVisible) {
        ReceiptAIConfirmationDialog(
            title = strings.discardChangesTitle,
            message = strings.discardChangesMessage,
            confirmLabel = strings.discard,
            dismissLabel = strings.stay,
            onConfirm = {
                isDiscardDialogVisible = false
                onBack()
            },
            onDismiss = { isDiscardDialogVisible = false }
        )
    }
}

@Composable
private fun TransactionFormCard(
    state: AddEditTransactionUiState,
    categories: List<String>,
    currencies: List<TransactionCurrencyOption>,
    isCategoryMenuExpanded: Boolean,
    isCurrencyMenuExpanded: Boolean,
    onCategoryMenuExpandedChange: (Boolean) -> Unit,
    onCurrencyMenuExpandedChange: (Boolean) -> Unit,
    onDateClick: () -> Unit,
    categoryLeadingIcon: ImageVector,
    amountHasBeenEdited: Boolean,
    onAmountEdited: () -> Unit,
    onStateChange: (AddEditTransactionUiState) -> Unit
) {
    val strings = receiptAIStrings()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = state.merchantName,
                onValueChange = { onStateChange(state.copy(merchantName = it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.merchantNameRequired) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = transactionFieldColors()
            )
            val amountError = amountHasBeenEdited && !state.isAmountValid()
            OutlinedTextField(
                value = state.totalAmount,
                onValueChange = {
                    onAmountEdited()
                    onStateChange(state.copy(totalAmount = it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.totalAmountRequired) },
                prefix = { Text(currencySymbol(state.currencyCode)) },
                isError = amountError,
                supportingText = if (amountError) {
                    { Text(strings.enterPositiveAmount) }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = transactionFieldColors()
            )
            TransactionTypeSelector(
                selectedType = state.transactionType,
                onTypeSelected = { type ->
                    onStateChange(state.copy(transactionType = type))
                }
            )
            ReadOnlyPickerField(
                value = state.dateText,
                label = strings.dateRequired,
                placeholder = strings.chooseDate,
                icon = Icons.Default.CalendarMonth,
                onClick = onDateClick
            )
            CustomDropdownField(
                value = state.category.ifBlank { "" }.let(strings.categoryLabel),
                label = strings.categoryRequired,
                placeholder = strings.selectCategory,
                leadingIcon = categoryLeadingIcon,
                options = categories.map { category ->
                    TransactionDropdownOption(
                        value = category,
                        label = strings.categoryLabel(category)
                    )
                },
                expanded = isCategoryMenuExpanded,
                onExpandedChange = onCategoryMenuExpandedChange,
                onOptionSelected = { category ->
                    onStateChange(state.copy(category = category))
                }
            )
            CustomDropdownField(
                value = state.currencyCode,
                label = strings.currencyRequired,
                placeholder = strings.selectCurrency,
                options = currencies.map { currency ->
                    TransactionDropdownOption(
                        value = currency.code,
                        label = "${currency.code} · ${strings.currencyName(currency.code)}"
                    )
                },
                expanded = isCurrencyMenuExpanded,
                onExpandedChange = onCurrencyMenuExpandedChange,
                onOptionSelected = { currencyCode ->
                    onStateChange(state.copy(currencyCode = currencyCode))
                }
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { onStateChange(state.copy(notes = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                label = { Text(strings.notesOptional) },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = transactionFieldColors()
            )
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    val strings = receiptAIStrings()
    val shape = RoundedCornerShape(14.dp)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = strings.transactionTypeRequired,
            style = MaterialTheme.typography.labelLarge,
            color = ReceiptAISecondaryText
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(shape)
                .border(
                    width = 1.dp,
                    color = ReceiptAISecondaryText.copy(alpha = 0.35f),
                    shape = shape
                )
        ) {
            TransactionTypeOption(
                type = TransactionType.EXPENSE,
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { onTypeSelected(TransactionType.EXPENSE) },
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 1.dp, max = 1.dp)
                    .background(ReceiptAISecondaryText.copy(alpha = 0.18f))
            )
            TransactionTypeOption(
                type = TransactionType.INCOME,
                selected = selectedType == TransactionType.INCOME,
                onClick = { onTypeSelected(TransactionType.INCOME) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TransactionTypeOption(
    type: TransactionType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = receiptAIStrings()
    val isIncome = type == TransactionType.INCOME
    val accent = if (isIncome) IncomeGreen else ValidationRed
    val selectedBackground = if (isIncome) {
        ReceiptAIMint.copy(alpha = 0.13f)
    } else {
        ValidationRed.copy(alpha = 0.10f)
    }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (selected) selectedBackground else ReceiptAISurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isIncome) {
                    Icons.Default.ArrowUpward
                } else {
                    Icons.Default.ArrowDownward
                },
                contentDescription = null,
                tint = if (selected) accent else ReceiptAISecondaryText,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (isIncome) strings.income else strings.expense,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) accent else ReceiptAISecondaryText
            )
        }
    }
}

@Composable
private fun ReadOnlyPickerField(
    value: String,
    label: String,
    placeholder: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val strings = receiptAIStrings()
    val fieldShape = RoundedCornerShape(14.dp)

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = strings.chooseDate,
                    tint = ReceiptAIDeepPurple
                )
            },
            singleLine = true,
            shape = fieldShape,
            colors = transactionFieldColors()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(fieldShape)
                .clickable(onClick = onClick)
        )
    }
}

@Composable
private fun CustomDropdownField(
    value: String,
    label: String,
    placeholder: String,
    options: List<TransactionDropdownOption>,
    leadingIcon: ImageVector? = null,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ReceiptAIDeepPurple
                    )
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = transactionFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth(),
            matchTextFieldWidth = true,
            shape = RoundedCornerShape(16.dp),
            containerColor = ReceiptAISurface,
            tonalElevation = 0.dp,
            shadowElevation = 4.dp
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = ReceiptAIPrimaryText
                        )
                    },
                    onClick = {
                        onOptionSelected(option.value)
                        onExpandedChange(false)
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun AddEditActions(
    secondaryLabel: String,
    isFormValid: Boolean,
    isSaving: Boolean,
    validationMessage: String?,
    hasAmountError: Boolean,
    onConfirmClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    val strings = receiptAIStrings()
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = ReceiptAIBackground,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            validationMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasAmountError) {
                        ValidationRed
                    } else {
                        ReceiptAISecondaryText
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                onClick = onConfirmClick,
                enabled = isFormValid && !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReceiptAIDeepPurple,
                    contentColor = ReceiptAIOnBrand,
                    disabledContainerColor = ReceiptAIDeepPurple.copy(alpha = 0.35f),
                    disabledContentColor = ReceiptAIOnBrand.copy(alpha = 0.75f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (isSaving) strings.saving else strings.confirmAndSave)
            }
            OutlinedButton(
                onClick = onSecondaryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ReceiptAIDeepPurple
                )
            ) {
                Text(secondaryLabel)
            }
        }
    }
}

@Composable
private fun transactionFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ReceiptAIDeepPurple,
    unfocusedBorderColor = ReceiptAISecondaryText.copy(alpha = 0.35f),
    focusedLabelColor = ReceiptAIDeepPurple,
    unfocusedLabelColor = ReceiptAISecondaryText,
    focusedTrailingIconColor = ReceiptAIDeepPurple,
    unfocusedTrailingIconColor = ReceiptAISecondaryText,
    focusedTextColor = ReceiptAIPrimaryText,
    unfocusedTextColor = ReceiptAIPrimaryText,
    focusedContainerColor = ReceiptAISurface,
    unfocusedContainerColor = ReceiptAISurface
)

@Composable
private fun CustomCalendarDialog(
    initialDateMillis: Long,
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val strings = receiptAIStrings()
    var selectedDateMillis by rememberSaveable(initialDateMillis) {
        mutableLongStateOf(startOfDay(initialDateMillis))
    }
    var displayedMonthMillis by rememberSaveable(initialDateMillis) {
        mutableLongStateOf(startOfMonth(initialDateMillis))
    }
    val displayedMonth = calendarOf(displayedMonthMillis)
    val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOffset = firstDayOffset(displayedMonth)
    val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReceiptAISystemBarsEffect()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.selectDate,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ReceiptAIPrimaryText
                        )
                        Text(
                            text = formatLongDate(selectedDateMillis),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ReceiptAISecondaryText
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.closeCalendar,
                            tint = ReceiptAISecondaryText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            displayedMonthMillis = addMonths(displayedMonthMillis, -1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = strings.previousMonth,
                            tint = ReceiptAIDeepPurple
                        )
                    }
                    Text(
                        text = formatMonth(displayedMonthMillis),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ReceiptAIDeepPurple,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            displayedMonthMillis = addMonths(displayedMonthMillis, 1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = strings.nextMonth,
                            tint = ReceiptAIDeepPurple
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdayLabels().forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ReceiptAISecondaryText,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                repeat(totalCells / 7) { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) { dayOfWeek ->
                            val cellIndex = week * 7 + dayOfWeek
                            val day = cellIndex - firstDayOffset + 1
                            if (day in 1..daysInMonth) {
                                val dayMillis = dateInMonth(displayedMonthMillis, day)
                                val isSelected = startOfDay(dayMillis) == selectedDateMillis
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) ReceiptAIDeepPurple else Color.Transparent
                                        )
                                        .clickable {
                                            selectedDateMillis = startOfDay(dayMillis)
                                            onDateSelected(dayMillis)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = if (isSelected) {
                                            ReceiptAIOnBrand
                                        } else {
                                            ReceiptAIPrimaryText
                                        }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                    if (week < totalCells / 7 - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = strings.tapDayToSelect,
                    style = MaterialTheme.typography.bodySmall,
                    color = ReceiptAIMint,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun AddEditTransactionUiState.isFormValid(): Boolean =
    merchantName.isNotBlank() &&
        isAmountValid() &&
        dateText.isNotBlank() &&
        dateMillis != null &&
        currencyCode.isNotBlank() &&
        category.isNotBlank()

private fun AddEditTransactionUiState.isAmountValid(): Boolean =
    parsePositiveAmount(totalAmount) != null

private fun AddEditTransactionUiState.missingRequiredFields(
    strings: ReceiptAIStrings
): List<String> = buildList {
    if (merchantName.isBlank()) add(strings.merchantNameRequired.removeSuffix(" *"))
    if (!isAmountValid()) add(strings.totalAmountRequired.removeSuffix(" *"))
    if (dateText.isBlank() || dateMillis == null) add(strings.dateRequired.removeSuffix(" *"))
    if (category.isBlank()) add(strings.categoryRequired.removeSuffix(" *"))
    if (currencyCode.isBlank()) add(strings.currencyRequired.removeSuffix(" *"))
}

private fun currencySymbol(currencyCode: String): String = when (currencyCode) {
    "EUR" -> "€"
    "GBP" -> "£"
    "PLN" -> "zł"
    "CAD" -> "CA\$"
    "AUD" -> "A\$"
    "JPY" -> "¥"
    else -> "\$"
}

private fun calendarOf(millis: Long): Calendar = Calendar.getInstance().apply {
    timeInMillis = millis
}

private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun startOfMonth(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun addMonths(millis: Long, amount: Int): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    add(Calendar.MONTH, amount)
}.timeInMillis.let(::startOfMonth)

private fun dateInMonth(monthMillis: Long, day: Int): Long = Calendar.getInstance().apply {
    timeInMillis = monthMillis
    set(Calendar.DAY_OF_MONTH, day)
}.timeInMillis

private fun firstDayOffset(month: Calendar): Int =
    (month.get(Calendar.DAY_OF_WEEK) - month.firstDayOfWeek + 7) % 7

private fun weekdayLabels(): List<String> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }
    val formatter = SimpleDateFormat("EEEEE", Locale.getDefault())
    return (0 until 7).map {
        val label = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        label
    }
}

private fun formatMonth(millis: Long): String =
    SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(calendarOf(millis).time)

private fun formatDate(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(calendarOf(millis).time)

private fun formatLongDate(millis: Long): String =
    SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(calendarOf(millis).time)

private val PreviewAddEditState = AddEditTransactionUiState(
    merchantName = "Sweetgreen",
    totalAmount = "14.50",
    dateText = "Aug 10, 2026",
    dateMillis = System.currentTimeMillis(),
    currencyCode = "USD",
    category = "Food & Dining",
    notes = "Lunch with the team."
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AddEditTransactionScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme() {
        AddEditTransactionScreen(
            state = PreviewAddEditState,
            mode = AddEditTransactionMode.ADD_EXPENSE,
            onBack = {},
            onStateChange = {},
            onConfirmSave = {}
        )
    }
}
