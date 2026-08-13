package com.receiptai.tracker.ui.theme

enum class ThemeMode(
    val storageValue: String,
    val label: String
) {
    SYSTEM_DEFAULT("system", "System Default"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromStorageValue(value: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM_DEFAULT
    }
}
