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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
private fun ConfettiBurst() {
    val particles = remember {
        List(45) {
            ConfettiParticle(
                angle = Random.nextFloat() * 360f,
                distance = Random.nextFloat() * 350f + 80f,
                size = Random.nextFloat() * 6f + 2f,
                color = listOf(
                    Color(0xFF4CAF50), Color(0xFF81C784), Color(0xFFA5D6A7),
                    Color(0xFF1B5E20), Color(0xFFFFC107), Color(0xFFFF9800),
                    Color(0xFF66BB6A), Color(0xFFAED581)
                ).random(),
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isRect = Random.nextBoolean()
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(200)
        progress.animateTo(1f, tween(1600, easing = EaseOut))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height * 0.20f
        particles.forEach { p ->
            val t = progress.value
            val rad = p.angle * PI.toFloat() / 180f
            val dist = p.distance * t
            val x = cx + cos(rad) * dist
            val gravity = 280f * t * t
            val y = cy + sin(rad) * dist + gravity
            val alpha = (1f - t * 0.9f).coerceIn(0f, 1f)

            if (p.isRect) {
                rotate(p.rotationSpeed * t, pivot = Offset(x, y)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x - p.size, y - p.size * 0.4f),
                        size = Size(p.size * 2f, p.size * 0.8f)
                    )
                }
            } else {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val angle: Float, val distance: Float, val size: Float,
    val color: Color, val rotationSpeed: Float, val isRect: Boolean
)

@Composable
private fun Sparkles() {
    val sparkles = remember {
        List(12) {
            SparkleData(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.55f,
                delayMs = Random.nextInt(0, 2500),
                durationMs = Random.nextInt(1200, 2800)
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")

    val phases = sparkles.mapIndexed { i, s ->
        infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(s.durationMs, delayMillis = s.delayMs, easing = LinearEasing),
                RepeatMode.Restart
            ), label = "sparkle_$i"
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        sparkles.forEachIndexed { i, s ->
            val t = phases[i].value
            val alpha = if (t < 0.2f) t / 0.2f else if (t > 0.8f) (1f - t) / 0.2f else 1f
            val sparkSize = 4f + 4f * sin(t * PI.toFloat())
            val cx = s.x * size.width
            val cy = s.y * size.height
            val a = alpha.coerceIn(0f, 1f) * 0.8f
            val sparkColor = Color(0xFF81C784).copy(alpha = a)

            drawLine(sparkColor, Offset(cx - sparkSize, cy), Offset(cx + sparkSize, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            drawLine(sparkColor, Offset(cx, cy - sparkSize), Offset(cx, cy + sparkSize), strokeWidth = 1.5f, cap = StrokeCap.Round)
            drawCircle(sparkColor, radius = 1.5f, center = Offset(cx, cy))
        }
    }
}

private data class SparkleData(val x: Float, val y: Float, val delayMs: Int, val durationMs: Int)

@Composable
private fun OrbitingDots(primaryColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbitRotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "orbitPulse"
    )

    Canvas(modifier = Modifier.size(120.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.width / 2f - 4f

        for (i in 0..2) {
            val angle = (rotation + i * 120f) * PI.toFloat() / 180f
            val dotSize = (2f + i * 0.5f) * pulse
            drawCircle(
                color = primaryColor.copy(alpha = 0.5f - i * 0.1f),
                radius = dotSize,
                center = Offset(cx + cos(angle) * radius, cy + sin(angle) * radius)
            )
        }
    }
}

@Composable
private fun RadialGlow(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "glowScale"
    )

    Canvas(
        modifier = Modifier
            .size(160.dp)
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
private fun AnimatedBadgeBounce(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }

    Box(modifier = Modifier.scale(scale.value)) {
        content()
    }
}

@Composable
private fun AnimatedCheckMark(color: Color) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(400)
        progress.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = Modifier.size(32.dp)) {
        val p = progress.value
        val path = Path()
        val startX = size.width * 0.18f
        val startY = size.height * 0.52f
        val midX = size.width * 0.42f
        val midY = size.height * 0.75f
        val endX = size.width * 0.82f
        val endY = size.height * 0.28f

        path.moveTo(startX, startY)
        if (p <= 0.5f) {
            val t = p / 0.5f
            path.lineTo(startX + (midX - startX) * t, startY + (midY - startY) * t)
        } else {
            path.lineTo(midX, midY)
            val t = (p - 0.5f) / 0.5f
            path.lineTo(midX + (endX - midX) * t, midY + (endY - midY) * t)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun OrderConfirmedScreen(
    orderId: String,
    totalAmount: String,
    onTrackOrder: () -> Unit,
    onContinueShopping: () -> Unit
) {
    val iconScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        iconScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ping")
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

    val shimmerProgress = remember { Animatable(-1f) }
    LaunchedEffect(Unit) {
        delay(800)
        shimmerProgress.animateTo(2f, tween(1400, easing = EaseInOut))
    }

    val iconRotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(600)
        iconRotation.animateTo(8f, tween(100))
        iconRotation.animateTo(-8f, tween(100))
        iconRotation.animateTo(5f, tween(80))
        iconRotation.animateTo(-5f, tween(80))
        iconRotation.animateTo(0f, tween(100))
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ConfettiBurst()
            Sparkles()

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
                        RadialGlow(color = primaryColor)

                        OrbitingDots(primaryColor = primaryColor)

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pingScale1)
                                .alpha(pingAlpha1)
                                .background(primaryColor.copy(alpha = 0.3f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pingScale2)
                                .alpha(pingAlpha2)
                                .background(primaryColor.copy(alpha = 0.2f), CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(iconScale.value)
                                .rotate(iconRotation.value)
                                .background(primaryColor.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedCheckMark(color = onPrimaryColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedFadeIn(delayMs = 120) {
                        Text(
                            text = "Order confirmed",
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
                            text = "Your selection from Safwa is being prepared\nwith care.",
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
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .drawWithContent {
                                    drawContent()
                                    val shimmerX = shimmerProgress.value * size.width
                                    val shimmerWidth = size.width * 0.5f
                                    drawRect(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.White.copy(alpha = 0.12f),
                                                Color.Transparent
                                            ),
                                            startX = shimmerX - shimmerWidth,
                                            endX = shimmerX + shimmerWidth
                                        )
                                    )
                                }
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
                                        text = if (orderId.isNotBlank()) "#$orderId" else "#SAF-204891",
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
                                        text = "Total",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (totalAmount.isNotBlank()) totalAmount else "SAR 1,238",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedBadgeBounce(delayMs = 480) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "SUCCESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32),
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
                            title = "Track order",
                            onContinue = onTrackOrder
                        )
                        Button(
                            onClick = onContinueShopping,
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
                                text = "Continue shopping",
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
private fun OrderConfirmedScreenPreview() {
    SafwaTheme {
        OrderConfirmedScreen("", "", {}, {})
    }
}
