package com.receiptai.tracker.presentation.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.receiptai.tracker.ui.theme.ReceiptAISecondaryText

private val SkeletonCardShape = RoundedCornerShape(22.dp)
private val SkeletonBlockShape = RoundedCornerShape(10.dp)

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        ),
        label = "shimmerOffset"
    )
    val base = ReceiptAISecondaryText.copy(alpha = 0.10f)
    val highlight = ReceiptAISecondaryText.copy(alpha = 0.22f)
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 300f)
    )
}

@Composable
private fun SkeletonBlock(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = SkeletonBlockShape
) {
    Spacer(
        modifier = modifier
            .size(width, height)
            .clip(shape)
            .background(shimmerBrush())
    )
}

@Composable
fun DashboardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonBlock(width = 88.dp, height = 26.dp)
        SkeletonBlock(width = 180.dp, height = 30.dp)
        SkeletonCardPlaceholder(height = 176.dp)
        SkeletonCardPlaceholder(height = 210.dp)
        repeat(3) {
            SkeletonRowPlaceholder()
        }
    }
}

@Composable
private fun SkeletonCardPlaceholder(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(SkeletonCardShape)
            .background(shimmerBrush())
    )
}

@Composable
private fun SkeletonRowPlaceholder() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBlock(width = 44.dp, height = 44.dp, shape = CircleShape)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SkeletonBlock(width = 150.dp, height = 14.dp)
            SkeletonBlock(width = 90.dp, height = 12.dp)
        }
    }
}
