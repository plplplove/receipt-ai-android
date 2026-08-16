package com.receiptai.tracker.domain.repository

import com.receiptai.tracker.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun migrateLegacyPreferences()

    suspend fun setThemeMode(value: String)

    suspend fun setDisplayCurrency(value: String)

    suspend fun setLanguage(value: String)

    suspend fun setAppPin(pin: String)

    suspend fun changeAppPin(currentPin: String, newPin: String): Boolean

    suspend fun disableAppLock(pin: String): Boolean

    suspend fun setBiometricUnlockEnabled(enabled: Boolean)
}
