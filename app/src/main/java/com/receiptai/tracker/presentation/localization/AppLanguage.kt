package com.receiptai.tracker.presentation.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(
    val storageValue: String,
    val nativeLabel: String
) {
    ENGLISH("en", "English"),
    UKRAINIAN("uk", "Українська");

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
    val confirmDetails: String,
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
    val retakePhoto: String,
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
    val currency: String,
    val security: String,
    val requirePinBiometrics: String,
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
    val receiptScanningSoon: String,
    val privacyPolicyMessage: String,
    val completeRequiredFields: String,
    val requiredFields: (String) -> String,
    val categoryLabel: (String) -> String,
    val currencyName: (String) -> String,
    val dateGroupLabel: (String) -> String,
    val themeModeLabel: (String) -> String
) {
    companion object {
        fun forLanguage(language: AppLanguage): ReceiptAIStrings = when (language) {
            AppLanguage.ENGLISH -> english()
            AppLanguage.UKRAINIAN -> ukrainian()
        }

        private fun english() = ReceiptAIStrings(
            navigationHome = "Home",
            navigationHistory = "History",
            navigationAnalytics = "Analytics",
            navigationSettings = "Settings",
            home = "Home",
            welcomeBack = "Welcome back",
            totalBalance = "Total Balance",
            convertedTo = { "Converted to $it" },
            readyForNextExpense = "Ready for your next expense",
            monthlySpending = { "Monthly Spending · $it" },
            spendingInsights = "Your spending insights will appear here",
            totalSpent = "Total Spent",
            recentTransactions = "Recent Transactions",
            seeAll = "See All",
            noTransactions = "No transactions yet",
            noMatchingTransactions = "No matching transactions",
            tapToAddFirstExpense = "Tap + to add your first expense.",
            merchant = "Merchant",
            notSpecified = "Not specified",
            uncategorized = "Uncategorized",
            transactions = "Transactions",
            searchTransactions = "Search transactions...",
            filterTransactions = "Filter transactions",
            details = "Details",
            transactionDetails = "Transaction details",
            date = "Date",
            account = "Account",
            category = "Category",
            notes = "Notes",
            noNotesAdded = "No notes added.",
            receipt = "Receipt",
            receiptImage = "Receipt image",
            completed = "Completed",
            original = { "Original: $it" },
            delete = "Delete",
            edit = "Edit",
            deleteTransactionTitle = "Delete transaction?",
            deleteTransactionMessage = "Are you sure you want to delete this transaction?",
            addExpense = "Add Expense",
            addExpenseSubtitle = "Choose how you want to log your expense.",
            scanReceipt = "Scan Receipt",
            addManually = "Add Manually",
            closeAddExpense = "Close add expense sheet",
            editExpense = "Edit Expense",
            confirmDetails = "Confirm Details",
            merchantNameRequired = "Merchant Name *",
            totalAmountRequired = "Total Amount *",
            enterPositiveAmount = "Enter a positive number, e.g. 14.50.",
            dateRequired = "Date *",
            categoryRequired = "Category *",
            currencyRequired = "Currency *",
            notesOptional = "Notes (Optional)",
            transactionTypeRequired = "Transaction type *",
            expense = "Expense",
            income = "Income",
            chooseDate = "Choose date",
            saving = "Saving…",
            confirmAndSave = "Confirm & Save",
            cancel = "Cancel",
            retakePhoto = "Retake Photo",
            discard = "Discard",
            stay = "Stay",
            discardChangesTitle = "Discard changes?",
            discardChangesMessage = "Your changes will not be saved. Are you sure you want to exit?",
            selectDate = "Select date",
            closeCalendar = "Close calendar",
            previousMonth = "Previous month",
            nextMonth = "Next month",
            tapDayToSelect = "Tap a day to select it",
            selectCategory = "Select category",
            selectCurrency = "Select currency",
            settings = "Settings",
            preferences = "Preferences",
            theme = "Theme",
            language = "Language",
            languageSubtitle = "Choose the language for ReceiptAI.",
            currency = "Currency",
            security = "Security",
            requirePinBiometrics = "Require PIN / Biometrics",
            dataManagement = "Data Management",
            exportToCsv = "Export to CSV",
            about = "About",
            privacyPolicy = "Privacy Policy",
            dangerZone = "Danger Zone",
            deleteAllData = "Delete All Data",
            deleteAllDataTitle = "Delete All Data?",
            deleteAllDataMessage = "Are you sure? This action cannot be undone and all your expenses will be permanently deleted.",
            chooseThemeSubtitle = "Choose how ReceiptAI should look.",
            displayCurrency = "Display currency",
            displayCurrencySubtitle = "All balances and analytics will be converted to this currency.",
            filterTransactionType = "Transaction type",
            all = "All",
            expenses = "Expenses",
            allTime = "All time",
            today = "Today",
            thisWeek = "This week",
            clear = "Clear",
            apply = "Apply",
            analytics = "Analytics",
            thisMonth = { "This month · $it" },
            spendingByCategory = "Spending by category",
            noAnalytics = "No analytics yet",
            analyticsEmptySubtitle = "Add transactions to see spending insights.",
            savedTransactions = { count -> if (count == 1) "1 saved transaction" else "$count saved transactions" },
            csvExported = "CSV exported successfully",
            csvExportFailed = "Unable to export CSV",
            transactionNotFound = "Transaction not found",
            saveFailed = "Unable to save transaction",
            deleteFailed = "Unable to delete transaction",
            deleteDataFailed = "Unable to delete data",
            receiptScanningSoon = "Receipt scanning is coming soon.",
            privacyPolicyMessage = "ReceiptAI stores your transaction data locally on this device so you can view your balance, history, and analytics. Your data is not shared with third parties.",
            completeRequiredFields = "Please complete all required fields.",
            requiredFields = { "Required: $it." },
            categoryLabel = ::englishCategory,
            currencyName = ::englishCurrencyName,
            dateGroupLabel = { it },
            themeModeLabel = ::englishThemeMode
        )

        private fun ukrainian() = ReceiptAIStrings(
            navigationHome = "Головна",
            navigationHistory = "Історія",
            navigationAnalytics = "Аналітика",
            navigationSettings = "Налаштування",
            home = "Головна",
            welcomeBack = "Вітаємо",
            totalBalance = "Загальний баланс",
            convertedTo = { "Конвертовано в $it" },
            readyForNextExpense = "Готово до наступної витрати",
            monthlySpending = { "Витрати за місяць · $it" },
            spendingInsights = "Тут з’явиться статистика витрат",
            totalSpent = "Всього витрачено",
            recentTransactions = "Останні транзакції",
            seeAll = "Усі",
            noTransactions = "Транзакцій ще немає",
            noMatchingTransactions = "Відповідних транзакцій немає",
            tapToAddFirstExpense = "Натисніть +, щоб додати першу витрату.",
            merchant = "Продавець",
            notSpecified = "Не вказано",
            uncategorized = "Без категорії",
            transactions = "Транзакції",
            searchTransactions = "Пошук транзакцій...",
            filterTransactions = "Фільтри транзакцій",
            details = "Деталі",
            transactionDetails = "Деталі транзакції",
            date = "Дата",
            account = "Рахунок",
            category = "Категорія",
            notes = "Нотатки",
            noNotesAdded = "Нотаток немає.",
            receipt = "Чек",
            receiptImage = "Зображення чека",
            completed = "Виконано",
            original = { "Оригінал: $it" },
            delete = "Видалити",
            edit = "Редагувати",
            deleteTransactionTitle = "Видалити транзакцію?",
            deleteTransactionMessage = "Ви впевнені, що хочете видалити цю транзакцію?",
            addExpense = "Додати витрату",
            addExpenseSubtitle = "Оберіть спосіб додавання витрати.",
            scanReceipt = "Сканувати чек",
            addManually = "Додати вручну",
            closeAddExpense = "Закрити вікно додавання",
            editExpense = "Редагувати витрату",
            confirmDetails = "Підтвердити дані",
            merchantNameRequired = "Назва продавця *",
            totalAmountRequired = "Сума *",
            enterPositiveAmount = "Введіть додатне число, наприклад 14.50.",
            dateRequired = "Дата *",
            categoryRequired = "Категорія *",
            currencyRequired = "Валюта *",
            notesOptional = "Нотатки (необов’язково)",
            transactionTypeRequired = "Тип транзакції *",
            expense = "Витрата",
            income = "Надходження",
            chooseDate = "Обрати дату",
            saving = "Збереження…",
            confirmAndSave = "Підтвердити й зберегти",
            cancel = "Скасувати",
            retakePhoto = "Пересканувати чек",
            discard = "Вийти",
            stay = "Залишитися",
            discardChangesTitle = "Скасувати зміни?",
            discardChangesMessage = "Зміни не буде збережено. Ви впевнені, що хочете вийти?",
            selectDate = "Оберіть дату",
            closeCalendar = "Закрити календар",
            previousMonth = "Попередній місяць",
            nextMonth = "Наступний місяць",
            tapDayToSelect = "Натисніть на день, щоб обрати його",
            selectCategory = "Оберіть категорію",
            selectCurrency = "Оберіть валюту",
            settings = "Налаштування",
            preferences = "Основні налаштування",
            theme = "Тема",
            language = "Мова",
            languageSubtitle = "Оберіть мову ReceiptAI.",
            currency = "Валюта",
            security = "Безпека",
            requirePinBiometrics = "Запитувати PIN / біометрію",
            dataManagement = "Керування даними",
            exportToCsv = "Експортувати в CSV",
            about = "Про додаток",
            privacyPolicy = "Політика конфіденційності",
            dangerZone = "Небезпечна зона",
            deleteAllData = "Видалити всі дані",
            deleteAllDataTitle = "Видалити всі дані?",
            deleteAllDataMessage = "Ви впевнені? Цю дію неможливо скасувати — усі витрати буде видалено назавжди.",
            chooseThemeSubtitle = "Оберіть вигляд ReceiptAI.",
            displayCurrency = "Валюта відображення",
            displayCurrencySubtitle = "Баланс і аналітика будуть конвертовані в цю валюту.",
            filterTransactionType = "Тип транзакції",
            all = "Усі",
            expenses = "Витрати",
            allTime = "За весь час",
            today = "Сьогодні",
            thisWeek = "Цього тижня",
            clear = "Очистити",
            apply = "Застосувати",
            analytics = "Аналітика",
            thisMonth = { "Цього місяця · $it" },
            spendingByCategory = "Витрати за категоріями",
            noAnalytics = "Аналітики ще немає",
            analyticsEmptySubtitle = "Додайте транзакції, щоб побачити статистику.",
            savedTransactions = { count -> "Збережених транзакцій: $count" },
            csvExported = "CSV успішно експортовано",
            csvExportFailed = "Не вдалося експортувати CSV",
            transactionNotFound = "Транзакцію не знайдено",
            saveFailed = "Не вдалося зберегти транзакцію",
            deleteFailed = "Не вдалося видалити транзакцію",
            deleteDataFailed = "Не вдалося видалити дані",
            receiptScanningSoon = "Сканування чеків буде доступне пізніше.",
            privacyPolicyMessage = "ReceiptAI зберігає дані транзакцій локально на цьому пристрої, щоб ви могли переглядати баланс, історію та аналітику. Ваші дані не передаються третім сторонам.",
            completeRequiredFields = "Заповніть усі обов’язкові поля.",
            requiredFields = { "Обов’язкові поля: $it." },
            categoryLabel = ::ukrainianCategory,
            currencyName = ::ukrainianCurrencyName,
            dateGroupLabel = ::ukrainianDateGroup,
            themeModeLabel = ::ukrainianThemeMode
        )

        private fun englishCategory(value: String): String = value

        private fun ukrainianCategory(value: String): String = when (value) {
            "Food & Dining" -> "Їжа та ресторани"
            "Transport" -> "Транспорт"
            "Shopping" -> "Покупки"
            "Health" -> "Здоров’я"
            "Housing" -> "Житло"
            "Utilities" -> "Комунальні послуги"
            "Other" -> "Інше"
            "All" -> "Усі"
            else -> value
        }

        private fun englishDateGroup(value: String): String = value

        private fun englishCurrencyName(value: String): String = when (value) {
            "USD" -> "US Dollar"
            "EUR" -> "Euro"
            "GBP" -> "British Pound"
            "PLN" -> "Polish Złoty"
            "CAD" -> "Canadian Dollar"
            "AUD" -> "Australian Dollar"
            "JPY" -> "Japanese Yen"
            else -> value
        }

        private fun ukrainianCurrencyName(value: String): String = when (value) {
            "USD" -> "Долар США"
            "EUR" -> "Євро"
            "GBP" -> "Британський фунт"
            "PLN" -> "Польський злотий"
            "CAD" -> "Канадський долар"
            "AUD" -> "Австралійський долар"
            "JPY" -> "Японська єна"
            else -> value
        }

        private fun ukrainianDateGroup(value: String): String = when (value) {
            "Today" -> "Сьогодні"
            "Yesterday" -> "Вчора"
            "This Week" -> "Цього тижня"
            "Older" -> "Раніше"
            else -> value
        }

        private fun englishThemeMode(value: String): String = when (value) {
            "system" -> "System Default"
            "light" -> "Light"
            "dark" -> "Dark"
            else -> value
        }

        private fun ukrainianThemeMode(value: String): String = when (value) {
            "system" -> "Системна"
            "light" -> "Світла"
            "dark" -> "Темна"
            else -> value
        }
    }
}

val LocalReceiptAIStrings = staticCompositionLocalOf {
    ReceiptAIStrings.forLanguage(AppLanguage.ENGLISH)
}

@Composable
fun receiptAIStrings(): ReceiptAIStrings = LocalReceiptAIStrings.current

fun ReceiptAIStrings.localizedError(message: String): String = when (message) {
    "Receipt scanning is coming soon." -> receiptScanningSoon
    "Transaction not found" -> transactionNotFound
    "Please complete all required fields." -> completeRequiredFields
    "Unable to save transaction" -> saveFailed
    "Unable to delete transaction" -> deleteFailed
    "Unable to delete data" -> deleteDataFailed
    else -> message
}
