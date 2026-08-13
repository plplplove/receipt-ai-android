package com.receiptai.tracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val ReceiptAILightBackground = Color(0xFFF7F7F8)
val ReceiptAIDarkBackground = Color(0xFF121016)
val ReceiptAIDeepPurple = Color(0xFF6750A4)
val ReceiptAIMint = Color(0xFF00BFA5)
val ReceiptAIOnBrand = Color.White
val ReceiptAILightSurface = Color.White
val ReceiptAIDarkSurface = Color(0xFF1E1B22)
val ReceiptAILightPrimaryText = Color(0xFF242229)
val ReceiptAIDarkPrimaryText = Color(0xFFF4EFF8)
val ReceiptAILightSecondaryText = Color(0xFF686572)
val ReceiptAIDarkSecondaryText = Color(0xFFCBC3D3)
val ReceiptAIError = Color(0xFFC62828)

@Immutable
data class ReceiptAIColorTokens(
    val background: Color,
    val surface: Color,
    val primaryText: Color,
    val secondaryText: Color
)

val LightReceiptAIColors = ReceiptAIColorTokens(
    background = ReceiptAILightBackground,
    surface = ReceiptAILightSurface,
    primaryText = ReceiptAILightPrimaryText,
    secondaryText = ReceiptAILightSecondaryText
)

val DarkReceiptAIColors = ReceiptAIColorTokens(
    background = ReceiptAIDarkBackground,
    surface = ReceiptAIDarkSurface,
    primaryText = ReceiptAIDarkPrimaryText,
    secondaryText = ReceiptAIDarkSecondaryText
)

val LocalReceiptAIColors = staticCompositionLocalOf { LightReceiptAIColors }

val ReceiptAIBackground: Color
    @Composable get() = LocalReceiptAIColors.current.background

val ReceiptAISurface: Color
    @Composable get() = LocalReceiptAIColors.current.surface

val ReceiptAIPrimaryText: Color
    @Composable get() = LocalReceiptAIColors.current.primaryText

val ReceiptAISecondaryText: Color
    @Composable get() = LocalReceiptAIColors.current.secondaryText

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = ReceiptAIDeepPurple
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
