package com.receiptai.tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = ReceiptAIMint,
    onSecondary = Color(0xFF00372F),
    tertiary = Color(0xFFFFB787),
    background = ReceiptAIDarkBackground,
    onBackground = ReceiptAIDarkPrimaryText,
    surface = ReceiptAIDarkSurface,
    onSurface = ReceiptAIDarkPrimaryText,
    surfaceVariant = Color(0xFF2A2730),
    onSurfaceVariant = ReceiptAIDarkSecondaryText,
    outline = Color(0xFF958E9C)
)

private val LightColorScheme = lightColorScheme(
    primary = ReceiptAIDeepPurple,
    secondary = ReceiptAIMint,
    tertiary = ReceiptAIDeepPurple,
    background = ReceiptAILightBackground,
    onBackground = ReceiptAILightPrimaryText,
    surface = ReceiptAILightSurface,
    onSurface = ReceiptAILightPrimaryText,
    surfaceVariant = Color(0xFFF0EEF3),
    onSurfaceVariant = ReceiptAILightSecondaryText,
    outline = Color(0xFFD6CFDF),
    onPrimary = ReceiptAILightSurface,
    onSecondary = ReceiptAILightPrimaryText,
    onTertiary = ReceiptAILightSurface
)

@Composable
fun ReceiptAIExpenseBudgetTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val tokens = if (darkTheme) DarkReceiptAIColors else LightReceiptAIColors

    CompositionLocalProvider(LocalReceiptAIColors provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
