package com.receiptai.tracker.domain.model

data class AppSettings(
    val themeMode: String,
    val displayCurrency: String,
    val language: String,
    val pinHash: String?,
    val biometricUnlockEnabled: Boolean
) {
    val isAppLockEnabled: Boolean get() = pinHash != null
}
