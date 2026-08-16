package com.receiptai.tracker.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.receiptai.tracker.domain.model.AppSettings
import com.receiptai.tracker.domain.repository.SettingsRepository
import com.receiptai.tracker.domain.security.PinHasher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "receiptai_settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pinHasher: PinHasher
) : SettingsRepository {

    private val dataStore = context.settingsDataStore
    private val legacyPreferences by lazy {
        context.getSharedPreferences(LEGACY_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override val settings: Flow<AppSettings> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences ->
            AppSettings(
                themeMode = preferences[THEME_MODE] ?: DEFAULT_THEME_MODE,
                displayCurrency = preferences[DISPLAY_CURRENCY] ?: DEFAULT_CURRENCY,
                language = preferences[LANGUAGE] ?: DEFAULT_LANGUAGE,
                pinHash = preferences[PIN_HASH],
                biometricUnlockEnabled = preferences[BIOMETRIC_UNLOCK] ?: false
            )
        }

    override suspend fun migrateLegacyPreferences() {
        val currentPreferences = runCatching { dataStore.data.first() }
            .getOrElse { throwable ->
                if (throwable is IOException) emptyPreferences() else throw throwable
            }
        if (currentPreferences.asMap().isNotEmpty()) {
            legacyPreferences.edit().clear().apply()
            return
        }
        val themeMode = legacyPreferences.getString(LEGACY_THEME_MODE_KEY, null)
        val displayCurrency = legacyPreferences.getString(LEGACY_DISPLAY_CURRENCY_KEY, null)
        val language = legacyPreferences.getString(LEGACY_LANGUAGE_KEY, null)
        if (themeMode == null && displayCurrency == null && language == null) return

        dataStore.edit { preferences ->
            themeMode?.let { preferences[THEME_MODE] = it }
            displayCurrency?.let { preferences[DISPLAY_CURRENCY] = it }
            language?.let { preferences[LANGUAGE] = it }
        }
        legacyPreferences.edit().clear().apply()
    }

    override suspend fun setThemeMode(value: String) {
        dataStore.edit { it[THEME_MODE] = value }
    }

    override suspend fun setDisplayCurrency(value: String) {
        dataStore.edit { it[DISPLAY_CURRENCY] = value }
    }

    override suspend fun setLanguage(value: String) {
        dataStore.edit { it[LANGUAGE] = value }
    }

    override suspend fun setAppPin(pin: String) {
        dataStore.edit { it[PIN_HASH] = pinHasher.hash(pin) }
    }

    override suspend fun changeAppPin(currentPin: String, newPin: String): Boolean {
        val currentHash = dataStore.data.first()[PIN_HASH] ?: return false
        if (!pinHasher.verify(currentPin, currentHash)) return false
        dataStore.edit { it[PIN_HASH] = pinHasher.hash(newPin) }
        return true
    }

    override suspend fun disableAppLock(pin: String): Boolean {
        val currentHash = dataStore.data.first()[PIN_HASH] ?: return true
        if (!pinHasher.verify(pin, currentHash)) return false
        dataStore.edit { preferences ->
            preferences.remove(PIN_HASH)
            preferences[BIOMETRIC_UNLOCK] = false
        }
        return true
    }

    override suspend fun setBiometricUnlockEnabled(enabled: Boolean) {
        dataStore.edit { it[BIOMETRIC_UNLOCK] = enabled }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DISPLAY_CURRENCY = stringPreferencesKey("display_currency")
        val LANGUAGE = stringPreferencesKey("language")
        val PIN_HASH = stringPreferencesKey("app_lock_pin_hash")
        val BIOMETRIC_UNLOCK = booleanPreferencesKey("biometric_unlock")

        const val DEFAULT_THEME_MODE = "system"
        const val DEFAULT_CURRENCY = "USD"
        const val DEFAULT_LANGUAGE = "en"
        const val LEGACY_PREFERENCES_NAME = "receiptai_preferences"
        const val LEGACY_THEME_MODE_KEY = "theme_mode"
        const val LEGACY_DISPLAY_CURRENCY_KEY = "display_currency"
        const val LEGACY_LANGUAGE_KEY = "language"
    }
}
