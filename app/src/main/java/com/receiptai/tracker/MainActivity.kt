package com.receiptai.tracker

import android.os.Bundle
import android.os.Build
import android.graphics.drawable.ColorDrawable
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.receiptai.tracker.presentation.dashboard.DashboardRoute
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDarkBackground
import com.receiptai.tracker.ui.theme.ReceiptAILightBackground
import com.receiptai.tracker.ui.theme.ThemeMode
import com.receiptai.tracker.presentation.localization.AppLanguage
import com.receiptai.tracker.presentation.localization.LocalReceiptAIStrings
import com.receiptai.tracker.presentation.localization.ReceiptAIStrings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsPreferences by lazy {
        getSharedPreferences("receiptai_preferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var storedThemeMode by rememberSaveable {
                mutableStateOf(
                    settingsPreferences.getString(
                        THEME_MODE_KEY,
                        ThemeMode.SYSTEM_DEFAULT.storageValue
                    )
                )
            }
            var storedDisplayCurrency by rememberSaveable {
                mutableStateOf(
                    settingsPreferences.getString(DISPLAY_CURRENCY_KEY, "USD") ?: "USD"
                )
            }
            var storedLanguage by rememberSaveable {
                mutableStateOf(
                    settingsPreferences.getString(LANGUAGE_KEY, AppLanguage.ENGLISH.storageValue)
                )
            }
            val appLanguage = AppLanguage.fromStorageValue(storedLanguage)
            val appStrings = ReceiptAIStrings.forLanguage(appLanguage)
            val themeMode = ThemeMode.fromStorageValue(storedThemeMode)
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
                // On newer Android versions edge-to-edge makes system bars
                // transparent by default. Explicitly paint both bars with the
                // active app background so there is no black/transparent strip.
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

            ReceiptAIExpenseBudgetTrackerTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                CompositionLocalProvider(
                    LocalReceiptAIStrings provides appStrings
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = ReceiptAIBackground
                    ) {
                        DashboardRoute(
                            viewModel = hiltViewModel(),
                            themeMode = themeMode,
                            onThemeModeChanged = { selectedMode ->
                                storedThemeMode = selectedMode.storageValue
                                settingsPreferences.edit()
                                    .putString(THEME_MODE_KEY, selectedMode.storageValue)
                                    .apply()
                            },
                            displayCurrency = storedDisplayCurrency,
                            onDisplayCurrencyChanged = { selectedCurrency ->
                                storedDisplayCurrency = selectedCurrency
                                settingsPreferences.edit()
                                    .putString(DISPLAY_CURRENCY_KEY, selectedCurrency)
                                    .apply()
                            },
                            appLanguage = appLanguage,
                            onLanguageChanged = { selectedLanguage ->
                                storedLanguage = selectedLanguage.storageValue
                                settingsPreferences.edit()
                                    .putString(LANGUAGE_KEY, selectedLanguage.storageValue)
                                    .apply()
                            }
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val THEME_MODE_KEY = "theme_mode"
        const val DISPLAY_CURRENCY_KEY = "display_currency"
        const val LANGUAGE_KEY = "language"
    }
}
