package com.tasneem.safwa.features.payment.presentation.view.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

private val PayPalNavy = Color(0xFF003087)
private val PayPalSky = Color(0xFF009CDE)
private val PayPalYellow = Color(0xFFFFC439)
private val PayPalBg = Color(0xFFF5F7FA)

private enum class PayPalStep { LOGIN, PROCESSING, CONFIRM }

/**
 * Simulates the PayPal Web Checkout flow inside a full-screen dialog.
 *
 * Steps:
 *  1. LOGIN  — user enters email/password (pre-filled for demo)
 *  2. PROCESSING — brief spinner mimicking network round-trip
 *  3. CONFIRM — shows order summary with "Pay Now" button
 *
 * Replace this dialog with the real Chrome Custom Tab call once you have a
 * PayPal backend that can create Orders and return the approval_url.
 */
@Composable
fun PayPalCheckoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(PayPalStep.LOGIN) }
    var email by remember { mutableStateOf("demo@example.com") }
    var password by remember { mutableStateOf("") }

    // Auto-advance from PROCESSING to CONFIRM after 1.5 s
    LaunchedEffect(step) {
        if (step == PayPalStep.PROCESSING) {
            delay(1500)
            step = PayPalStep.CONFIRM
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PayPalBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header bar ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                    // PayPal logotype centre
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(listOf(PayPalSky, PayPalNavy))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("P", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("Pay", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PayPalNavy)
                        Text("Pal", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PayPalSky)
                    }
                    // Lock icon right side
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(18.dp)
                    )
                }

                // ── Step content (animated) ────────────────────────────────
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    },
                    label = "paypal_step"
                ) { currentStep ->
                    when (currentStep) {
                        PayPalStep.LOGIN -> LoginStep(
                            email = email,
                            password = password,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onNext = { step = PayPalStep.PROCESSING },
                            onCancel = onDismiss
                        )
                        PayPalStep.PROCESSING -> ProcessingStep()
                        PayPalStep.CONFIRM -> ConfirmStep(
                            email = email,
                            onConfirm = onConfirm,
                            onCancel = onDismiss
                        )
                    }
                }
            }
        }
    }
}

// ── Step: Login ───────────────────────────────────────────────────────────────

@Composable
private fun LoginStep(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Log in to your PayPal account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PayPalNavy,
            textAlign = TextAlign.Center
        )

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PayPalSky,
                focusedLabelColor = PayPalSky
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PayPalSky,
                focusedLabelColor = PayPalSky
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        TextButton(onClick = {}) {
            Text("Forgot Password?", color = PayPalSky, fontSize = 14.sp)
        }

        // Log In button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PayPalYellow)
        ) {
            Text(
                text = "Log In",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFF003087)
            )
        }

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

        TextButton(onClick = onCancel) {
            Text("Cancel and return to Safwa", color = PayPalSky, fontSize = 14.sp)
        }

        // Footer
        Text(
            text = "Protected by PayPal's Buyer Protection",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// ── Step: Processing ─────────────────────────────────────────────────────────

@Composable
private fun ProcessingStep() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(color = PayPalSky, strokeWidth = 3.dp)
            Text(
                text = "Connecting to PayPal…",
                color = PayPalNavy,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

// ── Step: Confirm ─────────────────────────────────────────────────────────────

@Composable
private fun ConfirmStep(
    email: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00C853),
            modifier = Modifier.size(52.dp)
        )

        Text(
            text = "Logged in as",
            color = Color.Gray,
            fontSize = 13.sp
        )
        Text(
            text = email,
            fontWeight = FontWeight.Bold,
            color = PayPalNavy,
            fontSize = 15.sp
        )

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

        // Order summary card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Payment to",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Safwa Store",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PayPalNavy
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "Order Amount",
                        fontWeight = FontWeight.Bold,
                        color = PayPalNavy,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "PayPal Balance · USD",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Pay Now button
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PayPalYellow)
        ) {
            Text(
                text = "Pay Now",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = PayPalNavy
            )
        }

        TextButton(onClick = onCancel) {
            Text("Cancel and return to Safwa", color = PayPalSky, fontSize = 14.sp)
        }

        Text(
            text = "Protected by PayPal's Buyer Protection",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
