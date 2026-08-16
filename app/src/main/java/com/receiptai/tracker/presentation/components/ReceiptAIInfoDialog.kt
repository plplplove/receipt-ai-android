package com.receiptai.tracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.receiptai.tracker.ui.theme.ReceiptAIDeepPurple
import com.receiptai.tracker.ui.theme.ReceiptAIOnBrand
import com.receiptai.tracker.ui.theme.ReceiptAIPrimaryText
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText
import com.receiptai.tracker.ui.theme.ReceiptAISurface
import com.receiptai.tracker.ui.theme.ReceiptAISystemBarsEffect

@Composable
fun ReceiptAIInfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    icon: ImageVector = Icons.Default.Info,
    buttonLabel: String
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ReceiptAISystemBarsEffect()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ReceiptAISurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ReceiptAIDeepPurple,
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = ReceiptAIDeepPurple.copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                            .padding(14.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = ReceiptAIPrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReceiptAISecondaryText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReceiptAIDeepPurple,
                        contentColor = ReceiptAIOnBrand
                    )
                ) {
                    Text(buttonLabel)
                }
            }
        }
    }
}
