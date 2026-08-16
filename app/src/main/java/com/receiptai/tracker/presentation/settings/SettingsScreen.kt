package com.receiptai.tracker.presentation.settings
import androidx.biometric.BiometricManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.receiptai.tracker.presentation.dashboard.DashboardDestination
import com.receiptai.tracker.presentation.localization.AppLanguage
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import com.receiptai.tracker.presentation.components.ReceiptAIConfirmationDialog
import com.receiptai.tracker.presentation.components.ReceiptAIInfoDialog
import com.receiptai.tracker.presentation.navigation.AppSectionHeader
import com.receiptai.tracker.presentation.navigation.ReceiptAIBottomBar
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIError
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIMint
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISystemBarsEffect
import com.receiptai.tracker.ui.theme.ThemeMode

private val SettingsCardShape = RoundedCornerShape(20.dp)
private val SettingsRowShape = RoundedCornerShape(16.dp)

private data class DisplayCurrencyOption(
    val code: String,
    val symbol: String
)

private val DisplayCurrencyOptions = listOf(
    DisplayCurrencyOption("USD", "$"),
    DisplayCurrencyOption("EUR", "€"),
    DisplayCurrencyOption("GBP", "£"),
    DisplayCurrencyOption("PLN", "zł"),
    DisplayCurrencyOption("CAD", "CA$"),
    DisplayCurrencyOption("AUD", "A$"),
    DisplayCurrencyOption("JPY", "¥")
)

private enum class PinOverlayMode { SETUP, DISABLE, CHANGE }

private enum class PinStage { ENTER_CURRENT, ENTER_NEW, CONFIRM_NEW }

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.SYSTEM_DEFAULT,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    onDestinationSelected: (DashboardDestination) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onDeleteAllData: () -> Unit = {},
    onExportData: () -> Unit = {},
    appLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    currencyCode: String = "USD",
    onCurrencySelected: (String) -> Unit = {},
    appLockEnabled: Boolean = false,
    biometricUnlockEnabled: Boolean = false,
    securityViewModel: SettingsViewModel? = null
) {
    val context = LocalContext.current
    val isBiometricAvailable = remember {
        BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    var pinOverlayMode by rememberSaveable { mutableStateOf<PinOverlayMode?>(null) }
    var pinStage by rememberSaveable { mutableStateOf(PinStage.ENTER_CURRENT) }
    var pinError by rememberSaveable { mutableStateOf(false) }
    var pinErrorCount by rememberSaveable { mutableIntStateOf(0) }
    var firstNewPin by remember { mutableStateOf("") }
    var currentPin by remember { mutableStateOf("") }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showPrivacyPolicy by rememberSaveable { mutableStateOf(false) }
    var showThemeSelector by rememberSaveable { mutableStateOf(false) }
    var showCurrencySelector by rememberSaveable { mutableStateOf(false) }
    var showLanguageSelector by rememberSaveable { mutableStateOf(false) }
    val strings = receiptAIStrings()

    fun closePinOverlay() {
        pinOverlayMode = null
        pinStage = PinStage.ENTER_CURRENT
        pinError = false
        firstNewPin = ""
        currentPin = ""
    }

    val events = securityViewModel?.events
    if (events != null) {
        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    SecurityEvent.AppLockEnabled,
                    SecurityEvent.AppLockDisabled,
                    SecurityEvent.PinChanged -> closePinOverlay()
                    SecurityEvent.WrongPin -> {
                        pinError = true
                        pinErrorCount += 1
                    }
                }
            }
        }
    }

    fun onOverlayPinEntered(pin: String) {
        when (pinStage) {
            PinStage.ENTER_CURRENT -> when (pinOverlayMode) {
                PinOverlayMode.DISABLE -> securityViewModel?.disableAppLock(pin)
                PinOverlayMode.CHANGE -> {
                    currentPin = pin
                    pinError = false
                    pinStage = PinStage.ENTER_NEW
                }
                else -> closePinOverlay()
            }
            PinStage.ENTER_NEW -> {
                firstNewPin = pin
                pinError = false
                pinStage = PinStage.CONFIRM_NEW
            }
            PinStage.CONFIRM_NEW -> {
                if (pin == firstNewPin) {
                    when (pinOverlayMode) {
                        PinOverlayMode.SETUP -> securityViewModel?.enableAppLock(pin)
                        PinOverlayMode.CHANGE -> securityViewModel?.changeAppPin(currentPin, pin)
                        else -> closePinOverlay()
                    }
                } else {
                    pinError = true
                    pinStage = PinStage.ENTER_NEW
                }
            }
        }
    }

    val overlayStrings = when (pinStage) {
        PinStage.ENTER_CURRENT -> strings.appLockEnterCurrentPin to strings.appLockSubtitle
        PinStage.ENTER_NEW -> strings.appLockEnterNewPin to strings.appLockEnterNewPinSubtitle
        PinStage.CONFIRM_NEW -> strings.appLockConfirmPin to strings.appLockConfirmPinSubtitle
    }
    val overlayTitle = when (pinOverlayMode) {
        PinOverlayMode.SETUP -> strings.appLockSetupTitle
        PinOverlayMode.DISABLE -> strings.appLockDisableTitle
        PinOverlayMode.CHANGE -> strings.changePin
        else -> strings.appLockSetupTitle
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ReceiptAIBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ReceiptAIBottomBar(
                selectedDestination = DashboardDestination.SETTINGS,
                onDestinationSelected = onDestinationSelected,
                onAddClick = onAddExpenseClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                AppSectionHeader(title = strings.settings)
            }
            item {
                SettingsSection(title = strings.preferences) {
                    SettingsRow(
                        icon = if (themeMode == ThemeMode.LIGHT) {
                            Icons.Default.LightMode
                        } else {
                            Icons.Default.DarkMode
                        },
                        title = strings.theme,
                        value = strings.themeModeLabel(themeMode.storageValue),
                        onClick = { showThemeSelector = true }
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = strings.language,
                        value = strings.languageName(appLanguage.storageValue),
                        contentDescription = strings.language,
                        onClick = {
                            showLanguageSelector = true
                        }
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.AttachMoney,
                        title = strings.currency,
                        value = displayCurrencyLabel(currencyCode),
                        onClick = {
                            showCurrencySelector = true
                        }
                    )
                }
            }
            item {
                SettingsSection(title = strings.security) {
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = strings.requirePinBiometrics,
                        trailing = {
                            Switch(
                                checked = appLockEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        pinOverlayMode = PinOverlayMode.SETUP
                                        pinStage = PinStage.ENTER_NEW
                                    } else {
                                        pinOverlayMode = PinOverlayMode.DISABLE
                                        pinStage = PinStage.ENTER_CURRENT
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ReceiptAISurface,
                                    checkedTrackColor = ReceiptAIMint,
                                    checkedBorderColor = ReceiptAIMint,
                                    uncheckedThumbColor = ReceiptAISurface,
                                    uncheckedTrackColor = ReceiptAISecondaryText.copy(alpha = 0.28f),
                                    uncheckedBorderColor = ReceiptAISecondaryText.copy(alpha = 0.45f)
                                )
                            )
                        }
                    )
                    if (appLockEnabled) {
                        SettingsDivider()
                        if (isBiometricAvailable) {
                            SettingsRow(
                                icon = Icons.Default.Fingerprint,
                                title = strings.unlockWithBiometrics,
                                trailing = {
                                    Switch(
                                        checked = biometricUnlockEnabled,
                                        onCheckedChange = { enabled ->
                                            securityViewModel?.setBiometricUnlockEnabled(enabled)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ReceiptAISurface,
                                            checkedTrackColor = ReceiptAIMint,
                                            checkedBorderColor = ReceiptAIMint,
                                            uncheckedThumbColor = ReceiptAISurface,
                                            uncheckedTrackColor = ReceiptAISecondaryText.copy(alpha = 0.28f),
                                            uncheckedBorderColor = ReceiptAISecondaryText.copy(alpha = 0.45f)
                                        )
                                    )
                                }
                            )
                            SettingsDivider()
                        }
                        SettingsRow(
                            icon = Icons.Default.Password,
                            title = strings.changePin,
                            trailing = { SettingsChevron() },
                            onClick = {
                                pinOverlayMode = PinOverlayMode.CHANGE
                                pinStage = PinStage.ENTER_CURRENT
                            }
                        )
                    }
                }
            }
            item {
                SettingsSection(title = strings.dataManagement) {
                    SettingsRow(
                        icon = Icons.Default.FileDownload,
                        title = strings.exportToCsv,
                        trailing = { SettingsChevron() },
                        onClick = {
                            onExportData()
                        }
                    )
                }
            }
            item {
                SettingsSection(title = strings.about) {
                    SettingsRow(
                        icon = Icons.Default.Shield,
                        title = strings.privacyPolicy,
                        trailing = { SettingsChevron() },
                        onClick = {
                            showPrivacyPolicy = true
                        }
                    )
                }
            }
            item {
                SettingsSection(title = strings.dangerZone) {
                    SettingsRow(
                        icon = Icons.Default.DeleteOutline,
                        title = strings.deleteAllData,
                        titleColor = ReceiptAIError,
                        iconTint = ReceiptAIError,
                        onClick = { showDeleteConfirmation = true }
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        ReceiptAIConfirmationDialog(
            title = strings.deleteAllDataTitle,
            message = strings.deleteAllDataMessage,
            confirmLabel = strings.delete,
            dismissLabel = strings.cancel,
            icon = Icons.Default.DeleteOutline,
            confirmColor = ReceiptAIError,
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                onDeleteAllData()
            }
        )
    }

    if (showPrivacyPolicy) {
        ReceiptAIInfoDialog(
            title = strings.privacyPolicy,
            message = strings.privacyPolicyMessage,
            icon = Icons.Default.Shield,
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    if (showThemeSelector) {
        ThemeSelectionDialog(
            selectedMode = themeMode,
            onDismiss = { showThemeSelector = false },
            onModeSelected = { selectedMode ->
                showThemeSelector = false
                onThemeModeSelected(selectedMode)
            }
        )
    }

    if (showCurrencySelector) {
        CurrencySelectionDialog(
            selectedCurrency = currencyCode,
            onDismiss = { showCurrencySelector = false },
            onCurrencySelected = { selectedCurrency ->
                showCurrencySelector = false
                onCurrencySelected(selectedCurrency)
            }
        )
    }

    if (showLanguageSelector) {
        LanguageSelectionDialog(
            selectedLanguage = appLanguage,
            onDismiss = { showLanguageSelector = false },
            onLanguageSelected = { selectedLanguage ->
                showLanguageSelector = false
                onLanguageSelected(selectedLanguage)
            }
        )
    }

    if (pinOverlayMode != null) {
        PinEntryOverlay(
            title = overlayTitle,
            subtitle = overlayStrings.second,
            errorText = if (pinError) {
                if (pinStage == PinStage.CONFIRM_NEW || pinStage == PinStage.ENTER_NEW) {
                    strings.appLockPinsDontMatch
                } else {
                    strings.appLockWrongPin
                }
            } else {
                null
            },
            errorMarker = pinErrorCount,
            onPinEntered = ::onOverlayPinEntered,
            onCancel = ::closePinOverlay,
            cancelLabel = strings.cancel
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ReceiptAIPrimaryText,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = SettingsCardShape,
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    titleColor: Color = ReceiptAIPrimaryText,
    iconTint: Color = ReceiptAIDeepPurple,
    trailing: @Composable () -> Unit = {
        value?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = ReceiptAISecondaryText
            )
        }
    },
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .clip(SettingsRowShape)
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier.clickable(
                    role = Role.Button,
                    onClick = onClick
                )
            }
        )
        .then(
            if (contentDescription == null) {
                Modifier
            } else {
                Modifier.semantics {
                    this.contentDescription = contentDescription
                    role = Role.Button
                }
            }
        )
        .padding(horizontal = 16.dp, vertical = 14.dp)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        trailing()
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = ReceiptAISecondaryText.copy(alpha = 0.12f),
        thickness = 1.dp
    )
}

@Composable
private fun SettingsChevron() {
    Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = ReceiptAISecondaryText,
        modifier = Modifier.size(22.dp)
    )
}

@Composable
private fun ThemeSelectionDialog(
    selectedMode: ThemeMode,
    onDismiss: () -> Unit,
    onModeSelected: (ThemeMode) -> Unit
) {
    val strings = receiptAIStrings()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReceiptAISystemBarsEffect()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = strings.theme,
                    style = MaterialTheme.typography.titleLarge,
                    color = ReceiptAIPrimaryText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.chooseThemeSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
                Spacer(modifier = Modifier.height(18.dp))
                ThemeMode.entries.forEachIndexed { index, mode ->
                    ThemeOptionRow(
                        mode = mode,
                        selected = mode == selectedMode,
                        onClick = { onModeSelected(mode) }
                    )
                    if (index < ThemeMode.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = ReceiptAISecondaryText.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit
) {
    val strings = receiptAIStrings()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SettingsRowShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = ReceiptAIDeepPurple,
                unselectedColor = ReceiptAISecondaryText
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = strings.themeModeLabel(mode.storageValue),
            style = MaterialTheme.typography.bodyLarge,
            color = ReceiptAIPrimaryText,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private fun displayCurrencyLabel(currencyCode: String): String {
    val option = DisplayCurrencyOptions.firstOrNull { it.code == currencyCode }
    return option?.let { "${it.code} (${it.symbol})" } ?: currencyCode
}

@Composable
private fun CurrencySelectionDialog(
    selectedCurrency: String,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    val strings = receiptAIStrings()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReceiptAISystemBarsEffect()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = strings.displayCurrency,
                    style = MaterialTheme.typography.titleLarge,
                    color = ReceiptAIPrimaryText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.displayCurrencySubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
                Spacer(modifier = Modifier.height(18.dp))
                DisplayCurrencyOptions.forEachIndexed { index, option ->
                    CurrencyOptionRow(
                        option = option,
                        selected = option.code == selectedCurrency,
                        onClick = { onCurrencySelected(option.code) }
                    )
                    if (index < DisplayCurrencyOptions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = ReceiptAISecondaryText.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyOptionRow(
    option: DisplayCurrencyOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val strings = receiptAIStrings()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SettingsRowShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = ReceiptAIDeepPurple,
                unselectedColor = ReceiptAISecondaryText
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = strings.currencyName(option.code),
                style = MaterialTheme.typography.bodyLarge,
                color = ReceiptAIPrimaryText,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = "${option.code} (${option.symbol})",
                style = MaterialTheme.typography.bodySmall,
                color = ReceiptAISecondaryText
            )
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    selectedLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    val strings = receiptAIStrings()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReceiptAISystemBarsEffect()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = strings.language,
                    style = MaterialTheme.typography.titleLarge,
                    color = ReceiptAIPrimaryText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.languageSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
                Spacer(modifier = Modifier.height(18.dp))
                AppLanguage.entries.forEachIndexed { index, language ->
                    LanguageOptionRow(
                        language = language,
                        selected = language == selectedLanguage,
                        onClick = { onLanguageSelected(language) }
                    )
                    if (index < AppLanguage.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = ReceiptAISecondaryText.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SettingsRowShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = ReceiptAIDeepPurple,
                unselectedColor = ReceiptAISecondaryText
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = receiptAIStrings().languageName(language.storageValue),
            style = MaterialTheme.typography.bodyLarge,
            color = ReceiptAIPrimaryText,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SettingsScreenPreview() {
    ReceiptAIExpenseBudgetTrackerTheme() {
        SettingsScreen()
    }
}
