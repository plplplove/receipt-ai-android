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
val ReceiptAIBrandViolet = Color(0xFF8E6FD8)
val ReceiptAIBrandLavender = Color(0xFFE4D7FF)
val ReceiptAIBrandDeepViolet = Color(0xFF3A2E6B)
val ReceiptAIBrandNight = Color(0xFF241C45)

@Immutable
data class ReceiptAIColorTokens(
    val background: Color,
    val surface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val heroGradient: List<Color>,
    val surfaceGradient: List<Color>
)

val LightReceiptAIColors = ReceiptAIColorTokens(
    background = ReceiptAILightBackground,
    surface = ReceiptAILightSurface,
    primaryText = ReceiptAILightPrimaryText,
    secondaryText = ReceiptAILightSecondaryText,
    heroGradient = listOf(ReceiptAIBrandLavender, Color(0xFFBBA5E8), ReceiptAIBrandViolet),
    surfaceGradient = listOf(Color(0xFFFFFFFF), Color(0xFFF3EEFC))
)

val DarkReceiptAIColors = ReceiptAIColorTokens(
    background = ReceiptAIDarkBackground,
    surface = ReceiptAIDarkSurface,
    primaryText = ReceiptAIDarkPrimaryText,
    secondaryText = ReceiptAIDarkSecondaryText,
    heroGradient = listOf(Color(0xFF56479B), ReceiptAIBrandDeepViolet, ReceiptAIBrandNight),
    surfaceGradient = listOf(Color(0xFF241F2E), Color(0xFF1B1720))
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

val ReceiptAIHeroGradient: List<Color>
    @Composable get() = LocalReceiptAIColors.current.heroGradient

val ReceiptAISurfaceGradient: List<Color>
    @Composable get() = LocalReceiptAIColors.current.surfaceGradient
