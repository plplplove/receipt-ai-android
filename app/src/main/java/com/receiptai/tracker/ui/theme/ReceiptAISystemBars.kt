package com.receiptai.tracker.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
fun ReceiptAISystemBarsEffect(
    statusBarColor: Color = ReceiptAIBackground,
    navigationBarColor: Color = ReceiptAIBackground
) {
    val view = LocalView.current
    val window = (view.parent as? DialogWindowProvider)?.window
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    DisposableEffect(window, statusBarColor, navigationBarColor, isDarkTheme) {
        if (window != null) {
            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = navigationBarColor.toArgb()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !isDarkTheme
                isAppearanceLightNavigationBars = !isDarkTheme
            }
        }

        onDispose { }
    }
}
