package com.receiptai.tracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptai.tracker.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SecurityEvent {
    data object AppLockEnabled : SecurityEvent
    data object AppLockDisabled : SecurityEvent
    data object PinChanged : SecurityEvent
    data object WrongPin : SecurityEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<SecurityEvent>()
    val events: SharedFlow<SecurityEvent> = _events.asSharedFlow()

    fun enableAppLock(pin: String) {
        viewModelScope.launch {
            settingsRepository.setAppPin(pin)
            _events.emit(SecurityEvent.AppLockEnabled)
        }
    }

    fun disableAppLock(pin: String) {
        viewModelScope.launch {
            val disabled = settingsRepository.disableAppLock(pin)
            _events.emit(if (disabled) SecurityEvent.AppLockDisabled else SecurityEvent.WrongPin)
        }
    }

    fun changeAppPin(currentPin: String, newPin: String) {
        viewModelScope.launch {
            val changed = settingsRepository.changeAppPin(currentPin, newPin)
            _events.emit(if (changed) SecurityEvent.PinChanged else SecurityEvent.WrongPin)
        }
    }

    fun setBiometricUnlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricUnlockEnabled(enabled)
        }
    }
}
