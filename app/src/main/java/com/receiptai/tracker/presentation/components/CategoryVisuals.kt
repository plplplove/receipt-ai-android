package com.receiptai.tracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.luminance

enum class ExpenseCategoryKind(val accentArgb: Long) {
    FOOD(0xFF00A88F),
    TRANSPORT(0xFF4F6FC6),
    SHOPPING(0xFFA4517A),
    HEALTH(0xFF2E7D52),
    HOUSING(0xFF6750A4),
    UTILITIES(0xFFB26A00),
    OTHER(0xFF6C627D)
}

@Immutable
data class CategoryVisualStyle(
    val icon: ImageVector,
    val accent: Color,
    val container: Color
)

fun expenseCategoryKind(category: String): ExpenseCategoryKind = when {
    category.contains("food", ignoreCase = true) ||
        category.contains("dining", ignoreCase = true) -> ExpenseCategoryKind.FOOD
    category.contains("transport", ignoreCase = true) -> ExpenseCategoryKind.TRANSPORT
    category.contains("shopping", ignoreCase = true) -> ExpenseCategoryKind.SHOPPING
    category.contains("health", ignoreCase = true) -> ExpenseCategoryKind.HEALTH
    category.contains("housing", ignoreCase = true) ||
        category.contains("rent", ignoreCase = true) -> ExpenseCategoryKind.HOUSING
    category.contains("utilities", ignoreCase = true) -> ExpenseCategoryKind.UTILITIES
    else -> ExpenseCategoryKind.OTHER
}

fun categoryAccentArgb(category: String): Long = expenseCategoryKind(category).accentArgb

@Composable
fun categoryVisualStyle(category: String): CategoryVisualStyle {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return when (expenseCategoryKind(category)) {
        ExpenseCategoryKind.FOOD -> categoryStyle(
            icon = Icons.Default.Restaurant,
            lightAccent = Color(ExpenseCategoryKind.FOOD.accentArgb),
            lightContainer = Color(0xFFDDF7F1),
            darkAccent = Color(0xFF7BE7D5),
            darkContainer = Color(0xFF1D3A35),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.TRANSPORT -> categoryStyle(
            icon = Icons.Default.DirectionsCar,
            lightAccent = Color(ExpenseCategoryKind.TRANSPORT.accentArgb),
            lightContainer = Color(0xFFE8EEFF),
            darkAccent = Color(0xFFAFC2FF),
            darkContainer = Color(0xFF27324B),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.SHOPPING -> categoryStyle(
            icon = Icons.Default.ShoppingBag,
            lightAccent = Color(ExpenseCategoryKind.SHOPPING.accentArgb),
            lightContainer = Color(0xFFF8E7EF),
            darkAccent = Color(0xFFFFB6D1),
            darkContainer = Color(0xFF432936),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.HEALTH -> categoryStyle(
            icon = Icons.Default.Favorite,
            lightAccent = Color(ExpenseCategoryKind.HEALTH.accentArgb),
            lightContainer = Color(0xFFE2F4E9),
            darkAccent = Color(0xFF9AE7B6),
            darkContainer = Color(0xFF243A2D),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.HOUSING -> categoryStyle(
            icon = Icons.Default.Home,
            lightAccent = Color(ExpenseCategoryKind.HOUSING.accentArgb),
            lightContainer = Color(0xFFEFE9FA),
            darkAccent = Color(0xFFD0BCFF),
            darkContainer = Color(0xFF332A49),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.UTILITIES -> categoryStyle(
            icon = Icons.Default.Bolt,
            lightAccent = Color(ExpenseCategoryKind.UTILITIES.accentArgb),
            lightContainer = Color(0xFFFFF0D7),
            darkAccent = Color(0xFFFFCC8A),
            darkContainer = Color(0xFF46351F),
            isDarkTheme = isDarkTheme
        )
        ExpenseCategoryKind.OTHER -> categoryStyle(
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            lightAccent = Color(ExpenseCategoryKind.OTHER.accentArgb),
            lightContainer = Color(0xFFEFEDF2),
            darkAccent = Color(0xFFD1C7DA),
            darkContainer = Color(0xFF332F38),
            isDarkTheme = isDarkTheme
        )
    }
}

private fun categoryStyle(
    icon: ImageVector,
    lightAccent: Color,
    lightContainer: Color,
    darkAccent: Color,
    darkContainer: Color,
    isDarkTheme: Boolean
) = CategoryVisualStyle(
    icon = icon,
    accent = if (isDarkTheme) darkAccent else lightAccent,
    container = if (isDarkTheme) darkContainer else lightContainer
)
