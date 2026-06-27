package com.tasneem.safwa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tasneem.safwa.ui.onboarding.OnboardingScreen
import com.tasneem.safwa.ui.splash.SplashScreen

@Composable
fun SafwaApp() {
    var currentScreen by remember { mutableStateOf("splash") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "splash" -> {
                    SplashScreen(
                        onSplashComplete = {
                            currentScreen = "onboarding"
                        }
                    )
                }
                "onboarding" -> {
                    OnboardingScreen(
                        onFinish = {
                            // TODO: Navigate to Home or Main App Content
                        }
                    )
                }
            }
        }
    }
}
