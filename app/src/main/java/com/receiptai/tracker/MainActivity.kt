package com.receiptai.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
                scrim = Color.White.toArgb(),
                darkScrim = Color.White.toArgb()
            )
        )
        // Keep the system bars on the same surface on Android versions that
        // still honor explicit window bar colors.
        window.statusBarColor = ReceiptAIBackground.toArgb()
        window.navigationBarColor = Color.White.toArgb()
        setContent {
            ReceiptAIExpenseBudgetTrackerTheme(dynamicColor = false) {
                DashboardRoute(viewModel = hiltViewModel())
            }
        }
    }
}
