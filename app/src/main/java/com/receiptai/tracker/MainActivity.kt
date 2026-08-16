package com.receiptai.tracker

import android.os.Bundle
import android.os.Build
import android.graphics.drawable.ColorDrawable
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.receiptai.tracker.presentation.MainViewModel
import com.receiptai.tracker.presentation.dashboard.DashboardRoute
import com.receiptai.tracker.presentation.localization.AppLanguage
import com.receiptai.tracker.presentation.localization.LocalReceiptAIResources
import com.receiptai.tracker.presentation.localization.createReceiptAILanguageContext
import com.receiptai.tracker.presentation.lock.LockScreen
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDarkBackground
import com.receiptai.tracker.ui.theme.ReceiptAILightBackground
import com.receiptai.tracker.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mainViewModel.awaitSettingsLoaded()
            if (isDestroyed) return@launch
            setContent {
                val settings by mainViewModel.settings.collectAsStateWithLifecycle()
                val isLocked by mainViewModel.isLocked.collectAsStateWithLifecycle()
                val currentSettings = settings ?: return@setContent

                val appLanguage = AppLanguage.fromStorageValue(currentSettings.language)
                val localizedContext = remember(appLanguage) {
                    createReceiptAILanguageContext(appLanguage)
                }
                val themeMode = ThemeMode.fromStorageValue(currentSettings.themeMode)
                val darkTheme = when (themeMode) {
                    ThemeMode.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                }

                SideEffect {
                    val scrim = if (darkTheme) {
                        ReceiptAIDarkBackground.toArgb()
                    } else {
                        ReceiptAILightBackground.toArgb()
                    }
                    this@MainActivity.enableEdgeToEdge(
                        statusBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(scrim)
                        } else {
                            SystemBarStyle.light(scrim, scrim)
                        },
                        navigationBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(scrim)
                        } else {
                            SystemBarStyle.light(scrim, scrim)
                        }
                    )
                    window.statusBarColor = scrim
                    window.navigationBarColor = scrim
                    window.setBackgroundDrawable(ColorDrawable(scrim))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        window.isStatusBarContrastEnforced = false
                        window.isNavigationBarContrastEnforced = false
                    }
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }

                ReceiptAIExpenseBudgetTrackerTheme(darkTheme = darkTheme) {
                    CompositionLocalProvider(
                        LocalReceiptAIResources provides localizedContext.resources
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = ReceiptAIBackground
                        ) {
                            if (currentSettings.isAppLockEnabled && isLocked) {
                                LockScreen(
                                    biometricUnlockEnabled = currentSettings.biometricUnlockEnabled,
                                    onUnlocked = mainViewModel::unlock
                                )
                            } else {
                                DashboardRoute(
                                    viewModel = hiltViewModel(),
                                    themeMode = themeMode,
                                    onThemeModeChanged = { selectedMode ->
                                        mainViewModel.setThemeMode(selectedMode.storageValue)
                                    },
                                    displayCurrency = currentSettings.displayCurrency,
                                    onDisplayCurrencyChanged = { selectedCurrency ->
                                        mainViewModel.setDisplayCurrency(selectedCurrency)
                                    },
                                    appLanguage = appLanguage,
                                    onLanguageChanged = { selectedLanguage ->
                                        mainViewModel.setLanguage(selectedLanguage.storageValue)
                                    },
                                    appLockEnabled = currentSettings.isAppLockEnabled,
                                    biometricUnlockEnabled = currentSettings.biometricUnlockEnabled
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.onAppForeground()
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.onAppBackground()
    }
}
