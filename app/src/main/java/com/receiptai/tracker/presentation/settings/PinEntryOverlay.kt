package com.receiptai.tracker.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.receiptai.tracker.presentation.lock.PIN_LENGTH
import com.receiptai.tracker.presentation.lock.PinDots
import com.receiptai.tracker.presentation.lock.PinPad
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIError
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface

@Composable
fun PinEntryOverlay(
    title: String,
    subtitle: String,
    errorText: String?,
    errorMarker: Int,
    onPinEntered: (String) -> Unit,
    onCancel: () -> Unit,
    cancelLabel: String,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(subtitle) { pin = "" }
    LaunchedEffect(errorText, errorMarker) {
        if (errorText != null) pin = ""
    }

    fun onDigit(digit: Char) {
        if (pin.length >= PIN_LENGTH) return
        pin += digit
        if (pin.length == PIN_LENGTH) onPinEntered(pin)
    }

    fun onBackspace() {
        if (pin.isNotEmpty()) pin = pin.dropLast(1)
    }

    BackHandler(onBack = onCancel)

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ReceiptAISurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = ReceiptAIDeepPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ReceiptAIPrimaryText
            )
            Text(
                text = errorText ?: subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (errorText != null) ReceiptAIError else ReceiptAISecondaryText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            PinDots(filledCount = pin.length, isError = errorText != null)
            Spacer(modifier = Modifier.height(22.dp))
            PinPad(onDigit = ::onDigit, onBackspace = ::onBackspace)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = cancelLabel,
                style = MaterialTheme.typography.labelLarge,
                color = ReceiptAISecondaryText,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCancel)
                    .padding(8.dp)
            )
        }
    }
}
