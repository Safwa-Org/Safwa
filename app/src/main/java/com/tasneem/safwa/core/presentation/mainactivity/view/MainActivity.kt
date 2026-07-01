package com.tasneem.safwa.core.presentation.mainactivity.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.navigation.SafwaNavHost
import com.tasneem.safwa.core.navigation.StartDestination
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.core.theme.SafwaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // keep native splash screen until start destination is resolved
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.startDestination.value is StartDestination.Loading
        }

        enableEdgeToEdge()
        setContent {
            val isDarkMode by mainViewModel.isDarkMode.collectAsStateWithLifecycle()

            SafwaTheme(darkTheme = isDarkMode) {
                SafwaNavHost(mainViewModel = mainViewModel)
            }
        }
    }
}