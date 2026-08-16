package com.receiptai.tracker.presentation.lock

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.receiptai.tracker.presentation.localization.receiptAIStrings
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIError
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface

@Composable
fun LockScreen(
    biometricUnlockEnabled: Boolean,
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LockViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pin by remember { mutableStateOf("") }
    val strings = receiptAIStrings()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val canUseBiometrics = remember {
        BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }
    val isBiometricAvailable = biometricUnlockEnabled && canUseBiometrics && activity != null

    BackHandler {}

    fun showBiometricPrompt() {
        val hostActivity = activity ?: return
        val prompt = BiometricPrompt(
            hostActivity,
            ContextCompat.getMainExecutor(hostActivity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onUnlocked()
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(strings.biometricPromptTitle)
            .setNegativeButtonText(strings.usePin)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setConfirmationRequired(false)
            .build()
        prompt.authenticate(promptInfo)
    }

    LaunchedEffect(isBiometricAvailable) {
        if (isBiometricAvailable) showBiometricPrompt()
    }

    fun onDigit(digit: Char) {
        if (state.isWrongPin) {
            viewModel.consumeWrongPin()
            pin = ""
        }
        if (pin.length >= PIN_LENGTH) return
        pin += digit
        if (pin.length == PIN_LENGTH) {
            viewModel.submitPin(pin) {
                pin = ""
                onUnlocked()
            }
        }
    }

    fun onBackspace() {
        if (state.isWrongPin) {
            viewModel.consumeWrongPin()
            pin = ""
            return
        }
        if (pin.isNotEmpty()) pin = pin.dropLast(1)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ReceiptAIBackground)
            .pointerInput(Unit) { detectTapGestures { } }
            .statusBarsPadding()
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ReceiptAISurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = ReceiptAIDeepPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.appLockTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Text(
                text = if (state.isWrongPin) strings.appLockWrongPin else strings.appLockSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.isWrongPin) ReceiptAIError else ReceiptAISecondaryText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            PinDots(filledCount = pin.length, isError = state.isWrongPin)
            Spacer(modifier = Modifier.height(26.dp))
            PinPad(
                onDigit = ::onDigit,
                onBackspace = ::onBackspace,
                bottomStartContent = {
                    if (isBiometricAvailable) {
                        BiometricKeyButton(onClick = { showBiometricPrompt() })
                    }
                }
            )
        }
    }
}
