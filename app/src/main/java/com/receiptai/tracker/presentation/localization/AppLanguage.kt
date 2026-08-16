package com.receiptai.tracker.presentation.localization

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.receiptai.tracker.R
import java.util.Locale

enum class AppLanguage(
    val storageValue: String
) {
    ENGLISH("en"),
    UKRAINIAN("uk"),
    POLISH("pl"),
    GERMAN("de"),
    SPANISH("es");

    companion object {
        fun fromStorageValue(value: String?): AppLanguage =
            entries.firstOrNull { it.storageValue == value } ?: ENGLISH
    }
}

@Immutable
data class ReceiptAIStrings(
    val navigationHome: String,
    val navigationHistory: String,
    val navigationAnalytics: String,
    val navigationSettings: String,
    val home: String,
    val welcomeBack: String,
    val totalBalance: String,
    val convertedTo: (String) -> String,
    val readyForNextExpense: String,
    val monthlySpending: (String) -> String,
    val spendingInsights: String,
    val totalSpent: String,
    val recentTransactions: String,
    val seeAll: String,
    val noTransactions: String,
    val noMatchingTransactions: String,
    val tapToAddFirstExpense: String,
    val merchant: String,
    val notSpecified: String,
    val uncategorized: String,
    val transactions: String,
    val searchTransactions: String,
    val filterTransactions: String,
    val details: String,
    val transactionDetails: String,
    val date: String,
    val account: String,
    val category: String,
    val notes: String,
    val noNotesAdded: String,
    val receipt: String,
    val receiptImage: String,
    val completed: String,
    val original: (String) -> String,
    val delete: String,
    val edit: String,
    val deleteTransactionTitle: String,
    val deleteTransactionMessage: String,
    val addExpense: String,
    val addExpenseSubtitle: String,
    val scanReceipt: String,
    val addManually: String,
    val closeAddExpense: String,
    val editExpense: String,
    val navigateBack: String,
    val merchantNameRequired: String,
    val totalAmountRequired: String,
    val enterPositiveAmount: String,
    val dateRequired: String,
    val categoryRequired: String,
    val currencyRequired: String,
    val notesOptional: String,
    val transactionTypeRequired: String,
    val expense: String,
    val income: String,
    val chooseDate: String,
    val saving: String,
    val confirmAndSave: String,
    val cancel: String,
    val close: String,
    val mainAccount: String,
    val discard: String,
    val stay: String,
    val discardChangesTitle: String,
    val discardChangesMessage: String,
    val selectDate: String,
    val closeCalendar: String,
    val previousMonth: String,
    val nextMonth: String,
    val tapDayToSelect: String,
    val selectCategory: String,
    val selectCurrency: String,
    val settings: String,
    val preferences: String,
    val theme: String,
    val language: String,
    val languageSubtitle: String,
    val languageName: (String) -> String,
    val currency: String,
    val security: String,
    val requirePinBiometrics: String,
    val appLockTitle: String,
    val appLockSubtitle: String,
    val appLockWrongPin: String,
    val appLockSetupTitle: String,
    val appLockDisableTitle: String,
    val appLockEnterCurrentPin: String,
    val appLockEnterNewPin: String,
    val appLockEnterNewPinSubtitle: String,
    val appLockConfirmPin: String,
    val appLockConfirmPinSubtitle: String,
    val appLockPinsDontMatch: String,
    val changePin: String,
    val unlockWithBiometrics: String,
    val usePin: String,
    val biometricPromptTitle: String,
    val dataManagement: String,
    val exportToCsv: String,
    val about: String,
    val privacyPolicy: String,
    val dangerZone: String,
    val deleteAllData: String,
    val deleteAllDataTitle: String,
    val deleteAllDataMessage: String,
    val chooseThemeSubtitle: String,
    val displayCurrency: String,
    val displayCurrencySubtitle: String,
    val filterTransactionType: String,
    val all: String,
    val expenses: String,
    val allTime: String,
    val today: String,
    val thisWeek: String,
    val clear: String,
    val apply: String,
    val analytics: String,
    val thisMonth: (String) -> String,
    val spendingByCategory: String,
    val noAnalytics: String,
    val analyticsEmptySubtitle: String,
    val savedTransactions: (Int) -> String,
    val csvExported: String,
    val csvExportFailed: String,
    val transactionNotFound: String,
    val saveFailed: String,
    val deleteFailed: String,
    val deleteDataFailed: String,
    val loadFailed: String,
    val scanningReceipt: String,
    val receiptScanFailed: String,
    val offlineScanMessage: String,
    val privacyPolicyMessage: String,
    val completeRequiredFields: String,
    val requiredFields: (String) -> String,
    val categoryLabel: (String) -> String,
    val currencyName: (String) -> String,
    val dateGroupLabel: (String) -> String,
    val themeModeLabel: (String) -> String
) {
    companion object {
        fun from(resources: Resources): ReceiptAIStrings = ReceiptAIStrings(
            navigationHome = resources.string(R.string.navigation_home),
            navigationHistory = resources.string(R.string.navigation_history),
            navigationAnalytics = resources.string(R.string.navigation_analytics),
            navigationSettings = resources.string(R.string.navigation_settings),
            home = resources.string(R.string.home),
            welcomeBack = resources.string(R.string.welcome_back),
            totalBalance = resources.string(R.string.total_balance),
            convertedTo = { resources.string(R.string.converted_to, it) },
            readyForNextExpense = resources.string(R.string.ready_for_next_expense),
            monthlySpending = { resources.string(R.string.monthly_spending, it) },
            spendingInsights = resources.string(R.string.spending_insights),
            totalSpent = resources.string(R.string.total_spent),
            recentTransactions = resources.string(R.string.recent_transactions),
            seeAll = resources.string(R.string.see_all),
            noTransactions = resources.string(R.string.no_transactions),
            noMatchingTransactions = resources.string(R.string.no_matching_transactions),
            tapToAddFirstExpense = resources.string(R.string.tap_to_add_first_expense),
            merchant = resources.string(R.string.merchant),
            notSpecified = resources.string(R.string.not_specified),
            uncategorized = resources.string(R.string.uncategorized),
            transactions = resources.string(R.string.transactions),
            searchTransactions = resources.string(R.string.search_transactions),
            filterTransactions = resources.string(R.string.filter_transactions),
            details = resources.string(R.string.details),
            transactionDetails = resources.string(R.string.transaction_details),
            date = resources.string(R.string.date),
            account = resources.string(R.string.account),
            category = resources.string(R.string.category),
            notes = resources.string(R.string.notes),
            noNotesAdded = resources.string(R.string.no_notes_added),
            receipt = resources.string(R.string.receipt),
            receiptImage = resources.string(R.string.receipt_image),
            completed = resources.string(R.string.completed),
            original = { resources.string(R.string.original, it) },
            delete = resources.string(R.string.delete),
            edit = resources.string(R.string.edit),
            deleteTransactionTitle = resources.string(R.string.delete_transaction_title),
            deleteTransactionMessage = resources.string(R.string.delete_transaction_message),
            addExpense = resources.string(R.string.add_expense),
            addExpenseSubtitle = resources.string(R.string.add_expense_subtitle),
            scanReceipt = resources.string(R.string.scan_receipt),
            addManually = resources.string(R.string.add_manually),
            closeAddExpense = resources.string(R.string.close_add_expense),
            editExpense = resources.string(R.string.edit_expense),
            navigateBack = resources.string(R.string.navigate_back),
            merchantNameRequired = resources.string(R.string.merchant_name_required),
            totalAmountRequired = resources.string(R.string.total_amount_required),
            enterPositiveAmount = resources.string(R.string.enter_positive_amount),
            dateRequired = resources.string(R.string.date_required),
            categoryRequired = resources.string(R.string.category_required),
            currencyRequired = resources.string(R.string.currency_required),
            notesOptional = resources.string(R.string.notes_optional),
            transactionTypeRequired = resources.string(R.string.transaction_type_required),
            expense = resources.string(R.string.expense),
            income = resources.string(R.string.income),
            chooseDate = resources.string(R.string.choose_date),
            saving = resources.string(R.string.saving),
            confirmAndSave = resources.string(R.string.confirm_and_save),
            cancel = resources.string(R.string.cancel),
            discard = resources.string(R.string.discard),
            stay = resources.string(R.string.stay),
            discardChangesTitle = resources.string(R.string.discard_changes_title),
            discardChangesMessage = resources.string(R.string.discard_changes_message),
            selectDate = resources.string(R.string.select_date),
            closeCalendar = resources.string(R.string.close_calendar),
            previousMonth = resources.string(R.string.previous_month),
            nextMonth = resources.string(R.string.next_month),
            tapDayToSelect = resources.string(R.string.tap_day_to_select),
            selectCategory = resources.string(R.string.select_category),
            selectCurrency = resources.string(R.string.select_currency),
            settings = resources.string(R.string.settings),
            preferences = resources.string(R.string.preferences),
            theme = resources.string(R.string.theme),
            language = resources.string(R.string.language),
            languageSubtitle = resources.string(R.string.language_subtitle),
            languageName = { value -> resources.localizedLanguageName(value) },
            currency = resources.string(R.string.currency),
            security = resources.string(R.string.security),
            requirePinBiometrics = resources.string(R.string.require_pin_biometrics),
            appLockTitle = resources.string(R.string.lock_title),
            appLockSubtitle = resources.string(R.string.lock_subtitle),
            appLockWrongPin = resources.string(R.string.lock_wrong_pin),
            appLockSetupTitle = resources.string(R.string.lock_setup_title),
            appLockDisableTitle = resources.string(R.string.lock_disable_title),
            appLockEnterCurrentPin = resources.string(R.string.lock_enter_current_pin),
            appLockEnterNewPin = resources.string(R.string.lock_enter_new_pin),
            appLockEnterNewPinSubtitle = resources.string(R.string.lock_enter_new_pin_subtitle),
            appLockConfirmPin = resources.string(R.string.lock_confirm_pin),
            appLockConfirmPinSubtitle = resources.string(R.string.lock_confirm_pin_subtitle),
            appLockPinsDontMatch = resources.string(R.string.lock_pins_dont_match),
            changePin = resources.string(R.string.change_pin),
            unlockWithBiometrics = resources.string(R.string.unlock_with_biometrics),
            usePin = resources.string(R.string.use_pin),
            biometricPromptTitle = resources.string(R.string.biometric_prompt_title),
            dataManagement = resources.string(R.string.data_management),
            exportToCsv = resources.string(R.string.export_to_csv),
            about = resources.string(R.string.about),
            privacyPolicy = resources.string(R.string.privacy_policy),
            dangerZone = resources.string(R.string.danger_zone),
            deleteAllData = resources.string(R.string.delete_all_data),
            deleteAllDataTitle = resources.string(R.string.delete_all_data_title),
            deleteAllDataMessage = resources.string(R.string.delete_all_data_message),
            chooseThemeSubtitle = resources.string(R.string.choose_theme_subtitle),
            displayCurrency = resources.string(R.string.display_currency),
            displayCurrencySubtitle = resources.string(R.string.display_currency_subtitle),
            filterTransactionType = resources.string(R.string.filter_transaction_type),
            all = resources.string(R.string.all),
            expenses = resources.string(R.string.expenses),
            allTime = resources.string(R.string.all_time),
            today = resources.string(R.string.today),
            thisWeek = resources.string(R.string.this_week),
            clear = resources.string(R.string.clear),
            apply = resources.string(R.string.apply),
            analytics = resources.string(R.string.analytics),
            thisMonth = { resources.string(R.string.this_month, it) },
            spendingByCategory = resources.string(R.string.spending_by_category),
            noAnalytics = resources.string(R.string.no_analytics),
            analyticsEmptySubtitle = resources.string(R.string.analytics_empty_subtitle),
            savedTransactions = { count ->
                resources.getQuantityString(R.plurals.saved_transactions, count, count)
            },
            csvExported = resources.string(R.string.csv_exported),
            csvExportFailed = resources.string(R.string.csv_export_failed),
            transactionNotFound = resources.string(R.string.transaction_not_found),
            saveFailed = resources.string(R.string.save_failed),
            deleteFailed = resources.string(R.string.delete_failed),
            deleteDataFailed = resources.string(R.string.delete_data_failed),
            loadFailed = resources.string(R.string.unable_to_load_expenses),
            scanningReceipt = resources.string(R.string.scanning_receipt),
            receiptScanFailed = resources.string(R.string.receipt_scan_failed),
            offlineScanMessage = resources.string(R.string.offline_scan_message),
            privacyPolicyMessage = resources.string(R.string.privacy_policy_message),
            close = resources.string(R.string.close),
            mainAccount = resources.string(R.string.main_account),
            completeRequiredFields = resources.string(R.string.complete_required_fields),
            requiredFields = { resources.string(R.string.required_fields, it) },
            categoryLabel = { value -> resources.localizedCategory(value) },
            currencyName = { value -> resources.localizedCurrencyName(value) },
            dateGroupLabel = { value -> resources.localizedDateGroup(value) },
            themeModeLabel = { value -> resources.localizedThemeMode(value) }
        )
    }
}

private fun Resources.string(@StringRes id: Int, vararg formatArgs: Any): String =
    if (formatArgs.isEmpty()) getString(id) else getString(id, *formatArgs)

private fun Resources.localizedCategory(value: String): String = when (value) {
    "Food & Dining" -> string(R.string.category_food_dining)
    "Transport" -> string(R.string.category_transport)
    "Shopping" -> string(R.string.category_shopping)
    "Health" -> string(R.string.category_health)
    "Housing" -> string(R.string.category_housing)
    "Utilities" -> string(R.string.category_utilities)
    "Other" -> string(R.string.category_other)
    "All" -> string(R.string.all)
    else -> value
}

private fun Resources.localizedCurrencyName(value: String): String = when (value) {
    "USD" -> string(R.string.currency_usd)
    "EUR" -> string(R.string.currency_eur)
    "GBP" -> string(R.string.currency_gbp)
    "PLN" -> string(R.string.currency_pln)
    "CAD" -> string(R.string.currency_cad)
    "AUD" -> string(R.string.currency_aud)
    "JPY" -> string(R.string.currency_jpy)
    else -> value
}

private fun Resources.localizedLanguageName(value: String): String = when (value) {
    "en" -> string(R.string.language_english)
    "uk" -> string(R.string.language_ukrainian)
    "pl" -> string(R.string.language_polish)
    "de" -> string(R.string.language_german)
    "es" -> string(R.string.language_spanish)
    else -> value
}

private fun Resources.localizedDateGroup(value: String): String = when (value) {
    "Today" -> string(R.string.date_group_today)
    "Yesterday" -> string(R.string.date_group_yesterday)
    "This Week" -> string(R.string.date_group_this_week)
    "Older" -> string(R.string.date_group_older)
    else -> value
}

private fun Resources.localizedThemeMode(value: String): String = when (value) {
    "system" -> string(R.string.theme_system_default)
    "light" -> string(R.string.theme_light)
    "dark" -> string(R.string.theme_dark)
    else -> value
}

val LocalReceiptAIResources = staticCompositionLocalOf<Resources?> { null }

@Composable
fun receiptAIStrings(): ReceiptAIStrings {
    val resourceOverride = LocalReceiptAIResources.current
    val fallbackResources = LocalContext.current.resources
    return remember(resourceOverride, fallbackResources) {
        ReceiptAIStrings.from(resourceOverride ?: fallbackResources)
    }
}

fun Context.createReceiptAILanguageContext(language: AppLanguage): Context {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(Locale.forLanguageTag(language.storageValue))
    return createConfigurationContext(configuration)
}

fun ReceiptAIStrings.localizedError(message: String): String = when (message) {
    "Transaction not found" -> transactionNotFound
    "Please complete all required fields." -> completeRequiredFields
    "Unable to save transaction" -> saveFailed
    "Unable to delete transaction" -> deleteFailed
    "Unable to delete data" -> deleteDataFailed
    "Unable to load expenses" -> loadFailed
    "Couldn't read the receipt, please add details manually." -> receiptScanFailed
    "No internet connection. Please enter the details manually." -> offlineScanMessage
    else -> message
}
