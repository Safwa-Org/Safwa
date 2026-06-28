package com.tasneem.safwa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tasneem.safwa.ui.onboarding.OnboardingScreen

@Composable
fun SafwaApp() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            OnboardingScreen(
                onFinish = {
                    // TODO: Navigate to Home or Main App Content
                }
            )
        }
    }
}
