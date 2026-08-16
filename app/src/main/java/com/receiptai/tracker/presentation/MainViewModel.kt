package com.receiptai.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptai.tracker.domain.model.AppSettings
import com.receiptai.tracker.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var backgroundedAtMillis = 0L

    init {
        viewModelScope.launch {
            settingsRepository.migrateLegacyPreferences()
            settingsRepository.settings
                .onEach { value ->
                    _settings.value = value
                    if (!value.isAppLockEnabled) _isLocked.value = false
                }
                .launchIn(this)
        }
    }

    suspend fun awaitSettingsLoaded() {
        settings.first { it != null }
    }

    fun setThemeMode(value: String) {
        viewModelScope.launch { settingsRepository.setThemeMode(value) }
    }

    fun setDisplayCurrency(value: String) {
        viewModelScope.launch { settingsRepository.setDisplayCurrency(value) }
    }

    fun setLanguage(value: String) {
        viewModelScope.launch { settingsRepository.setLanguage(value) }
    }

    fun onAppBackground() {
        backgroundedAtMillis = System.currentTimeMillis()
    }

    fun onAppForeground() {
        val currentSettings = _settings.value ?: return
        if (!currentSettings.isAppLockEnabled) return
        if (backgroundedAtMillis == 0L) return
        val awayForMillis = System.currentTimeMillis() - backgroundedAtMillis
        if (awayForMillis >= LOCK_DELAY_MILLIS) _isLocked.value = true
    }

    fun unlock() {
        _isLocked.value = false
    }

    private companion object {
        const val LOCK_DELAY_MILLIS = 300_000L
    }
}
