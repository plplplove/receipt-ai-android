package com.receiptai.tracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

fun categoryVisualStyle(category: String): CategoryVisualStyle =
    when (expenseCategoryKind(category)) {
        ExpenseCategoryKind.FOOD -> CategoryVisualStyle(
            icon = Icons.Default.Restaurant,
            accent = Color(ExpenseCategoryKind.FOOD.accentArgb),
            container = Color(0xFFDDF7F1)
        )
        ExpenseCategoryKind.TRANSPORT -> CategoryVisualStyle(
            icon = Icons.Default.DirectionsCar,
            accent = Color(ExpenseCategoryKind.TRANSPORT.accentArgb),
            container = Color(0xFFE8EEFF)
        )
        ExpenseCategoryKind.SHOPPING -> CategoryVisualStyle(
            icon = Icons.Default.ShoppingBag,
            accent = Color(ExpenseCategoryKind.SHOPPING.accentArgb),
            container = Color(0xFFF8E7EF)
        )
        ExpenseCategoryKind.HEALTH -> CategoryVisualStyle(
            icon = Icons.Default.Favorite,
            accent = Color(ExpenseCategoryKind.HEALTH.accentArgb),
            container = Color(0xFFE2F4E9)
        )
        ExpenseCategoryKind.HOUSING -> CategoryVisualStyle(
            icon = Icons.Default.Home,
            accent = Color(ExpenseCategoryKind.HOUSING.accentArgb),
            container = Color(0xFFEFE9FA)
        )
        ExpenseCategoryKind.UTILITIES -> CategoryVisualStyle(
            icon = Icons.Default.Bolt,
            accent = Color(ExpenseCategoryKind.UTILITIES.accentArgb),
            container = Color(0xFFFFF0D7)
        )
        ExpenseCategoryKind.OTHER -> CategoryVisualStyle(
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            accent = Color(ExpenseCategoryKind.OTHER.accentArgb),
            container = Color(0xFFEFEDF2)
        )
    }
