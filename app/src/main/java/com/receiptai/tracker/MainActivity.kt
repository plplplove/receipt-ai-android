package com.receiptai.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import com.receiptai.tracker.ui.theme.ReceiptAIBackground
import com.receiptai.tracker.presentation.dashboard.DashboardRoute
import com.receiptai.tracker.ui.theme.ReceiptAIExpenseBudgetTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = ReceiptAIBackground.toArgb(),
                darkScrim = ReceiptAIBackground.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = ReceiptAIBackground.toArgb(),
                darkScrim = ReceiptAIBackground.toArgb()
            )
        )
        setContent {
            ReceiptAIExpenseBudgetTrackerTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ReceiptAIBackground
                ) {
                    DashboardRoute(viewModel = hiltViewModel())
                }
            }
        }
    }
}
