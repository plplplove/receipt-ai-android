package com.receiptai.tracker.ui.theme

enum class ThemeMode(
    val storageValue: String
) {
    SYSTEM_DEFAULT("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorageValue(value: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM_DEFAULT
    }
}
