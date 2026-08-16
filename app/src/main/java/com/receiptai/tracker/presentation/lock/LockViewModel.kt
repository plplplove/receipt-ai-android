package com.receiptai.tracker.presentation.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptai.tracker.domain.repository.SettingsRepository
import com.receiptai.tracker.domain.security.PinHasher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LockUiState(
    val isWrongPin: Boolean = false
)

@HiltViewModel
class LockViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val pinHasher: PinHasher
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    fun submitPin(pin: String, onUnlocked: () -> Unit) {
        viewModelScope.launch {
            val storedHash = settingsRepository.settings.first().pinHash
            val isCorrect = storedHash != null && pinHasher.verify(pin, storedHash)
            if (isCorrect) {
                _state.update { it.copy(isWrongPin = false) }
                onUnlocked()
            } else {
                _state.update { it.copy(isWrongPin = true) }
            }
        }
    }

    fun consumeWrongPin() {
        _state.update { it.copy(isWrongPin = false) }
    }
}
