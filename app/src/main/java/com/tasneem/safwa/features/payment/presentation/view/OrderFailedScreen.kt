package com.tasneem.safwa.features.payment.presentation.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.theme.SafwaTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
private fun FallingFragments() {
    val fragments = remember {
        List(30) {
            FragmentData(
                startX = Random.nextFloat(),
                angle = Random.nextFloat() * 80f - 40f,
                distance = Random.nextFloat() * 250f + 60f,
                rotation = Random.nextFloat() * 540f - 270f,
                size = Random.nextFloat() * 5f + 2f,
                color = listOf(
                    Color(0xFFE57373), Color(0xFFEF5350), Color(0xFFF44336),
                    Color(0xFFD32F2F), Color(0xFFB71C1C), Color(0xFFFF8A80),
                    Color(0xFFFF5252), Color(0xFFE53935)
                ).random(),
                isRect = Random.nextBoolean()
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(300)
        progress.animateTo(1f, tween(2000, easing = EaseOut))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height * 0.20f
        fragments.forEach { f ->
            val t = progress.value
            val rad = (f.angle - 90f) * PI.toFloat() / 180f
            val explodeDist = f.distance * (t.coerceAtMost(0.4f) / 0.4f)
            val x = cx + (f.startX - 0.5f) * size.width * 0.3f + cos(rad) * explodeDist
            val gravity = if (t > 0.4f) 450f * (t - 0.4f) * (t - 0.4f) else 0f
            val y = cy + sin(rad) * explodeDist + gravity
            val alpha = if (t > 0.6f) (1f - (t - 0.6f) / 0.4f).coerceIn(0f, 1f) else 1f

            if (f.isRect) {
                rotate(f.rotation * t, pivot = Offset(x, y)) {
                    drawRect(
                        color = f.color.copy(alpha = alpha),
                        topLeft = Offset(x - f.size, y - f.size * 0.4f),
                        size = Size(f.size * 2f, f.size * 0.8f)
                    )
                }
            } else {
                drawCircle(
                    color = f.color.copy(alpha = alpha),
                    radius = f.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class FragmentData(
    val startX: Float, val angle: Float, val distance: Float,
    val rotation: Float, val size: Float, val color: Color,
    val isRect: Boolean
)

@Composable
private fun CrackLines() {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(250)
        progress.animateTo(1f, tween(600, easing = EaseOut))
    }
    val fadeOut = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        delay(1200)
        fadeOut.animateTo(0f, tween(800, easing = EaseInOut))
    }

    val cracks = remember {
        List(6) {
            CrackData(
                angle = it * 60f + Random.nextFloat() * 20f - 10f,
                length = Random.nextFloat() * 60f + 30f,
                zigzag = Random.nextFloat() * 10f - 5f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height * 0.20f
        val t = progress.value
        val a = fadeOut.value

        cracks.forEach { crack ->
            val rad = crack.angle * PI.toFloat() / 180f
            val len = crack.length * t
            val path = Path().apply {
                moveTo(cx, cy)
                val midX = cx + cos(rad) * len * 0.5f + crack.zigzag
                val midY = cy + sin(rad) * len * 0.5f - crack.zigzag
                val endX = cx + cos(rad) * len
                val endY = cy + sin(rad) * len
                lineTo(midX, midY)
                lineTo(endX, endY)
            }
            drawPath(
                path = path,
                color = Color(0xFFE57373).copy(alpha = 0.4f * a),
                style = Stroke(width = 1.5f, cap = StrokeCap.Round)
            )
        }
    }
}

private data class CrackData(val angle: Float, val length: Float, val zigzag: Float)

@Composable
private fun DangerGlow(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dangerGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f, targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label = "dangerGlowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label = "dangerGlowScale"
    )

    Canvas(
        modifier = Modifier
            .size(150.dp)
            .scale(glowScale)
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = glowAlpha), Color.Transparent),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width / 2
            )
        )
    }
}

@Composable
private fun AnimatedXMark(color: Color) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(400)
        progress.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = Modifier.size(32.dp)) {
        val p = progress.value
        val pad = size.width * 0.22f

        if (p > 0f) {
            val t1 = (p / 0.5f).coerceAtMost(1f)
            drawLine(
                color = color,
                start = Offset(pad, pad),
                end = Offset(pad + (size.width - 2 * pad) * t1, pad + (size.height - 2 * pad) * t1),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        if (p > 0.5f) {
            val t2 = ((p - 0.5f) / 0.5f).coerceAtMost(1f)
            drawLine(
                color = color,
                start = Offset(size.width - pad, pad),
                end = Offset(size.width - pad - (size.width - 2 * pad) * t2, pad + (size.height - 2 * pad) * t2),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun AnimatedFadeIn(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(24f) }
    val scaleAnim = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        alpha.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        offsetY.animateTo(0f, tween(450, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        scaleAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha.value
                translationY = offsetY.value * density
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
    ) {
        content()
    }
}

@Composable
private fun AnimatedSlideUp(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(60f) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        alpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha.value
                translationY = offsetY.value * density
            }
    ) {
        content()
    }
}

@Composable
private fun AnimatedBadgePulse(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        scale.animateTo(1.2f, tween(200, easing = EaseOut))
        scale.animateTo(0.9f, tween(100))
        scale.animateTo(1.05f, tween(100))
        scale.animateTo(1f, tween(100))
    }

    Box(modifier = Modifier.scale(scale.value)) {
        content()
    }
}

@Composable
fun OrderFailedScreen(
    onRetryPayment: () -> Unit,
    onContactSupport: () -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(200)
        val offsets = listOf(-6f, 6f, -6f, 6f, -4f, 4f, -2f, 2f, 0f)
        offsets.forEach { target ->
            shakeOffset.animateTo(target, tween(70, easing = EaseInOut))
        }
    }

    val iconScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        iconScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "errorPing")
    val pingScale1 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseOut), RepeatMode.Restart),
        label = "pingScale1"
    )
    val pingAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseOut), RepeatMode.Restart),
        label = "pingAlpha1"
    )
    val pingScale2 by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(1400, delayMillis = 700, easing = EaseOut), RepeatMode.Restart),
        label = "pingScale2"
    )
    val pingAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, delayMillis = 700, easing = EaseOut), RepeatMode.Restart),
        label = "pingAlpha2"
    )

    val screenShake = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(800)
        screenShake.animateTo(3f, tween(50))
        screenShake.animateTo(-3f, tween(50))
        screenShake.animateTo(2f, tween(50))
        screenShake.animateTo(-1f, tween(50))
        screenShake.animateTo(0f, tween(80))
    }

    val errorColor = Color(0xFFC62828)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .graphicsLayer { translationX = screenShake.value * density }
        ) {
            FallingFragments()
            CrackLines()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        DangerGlow(color = errorColor)

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pingScale1)
                                .alpha(pingAlpha1)
                                .background(Color(0xFFE57373).copy(alpha = 0.3f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pingScale2)
                                .alpha(pingAlpha2)
                                .background(Color(0xFFE57373).copy(alpha = 0.2f), CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(iconScale.value)
                                .graphicsLayer {
                                    translationX = shakeOffset.value * density
                                    rotationZ = shakeOffset.value * 0.8f
                                }
                                .background(Color(0xFFE57373).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(errorColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedXMark(color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedFadeIn(delayMs = 120) {
                        Text(
                            text = "Payment failed",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = MaterialTheme.typography.displayMedium.fontFamily
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedFadeIn(delayMs = 220) {
                        Text(
                            text = "We couldn't process your payment. No charge\nwas made.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedFadeIn(delayMs = 340) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ORDER",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "#SAF-204891",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Amount due",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "SAR 1,238",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Payment method",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Visa · 4242",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedBadgePulse(delayMs = 480) {
                        Box(
                            modifier = Modifier
                                .background(
                                    errorColor.copy(alpha = 0.1f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "FAILED",
                                style = MaterialTheme.typography.labelSmall,
                                color = errorColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                AnimatedSlideUp(delayMs = 560) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomButon(
                            title = "Retry payment",
                            onContinue = onRetryPayment
                        )
                        Button(
                            onClick = onContactSupport,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(28.dp)
                                ),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(
                                text = "Contact support",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderFailedScreenPreview() {
    SafwaTheme {
        OrderFailedScreen({}, {})
    }
}
