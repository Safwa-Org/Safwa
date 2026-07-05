package com.tasneem.safwa.core.presentation.mainactivity.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.features.settings.savedaddresses.data.sync.SessionSyncManager
import com.tasneem.safwa.core.navigation.SafwaNavHost
import com.tasneem.safwa.core.navigation.StartDestination
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.core.theme.SafwaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // Keep AppCompatActivity for language switching

    @Inject
    lateinit var syncManager: SessionSyncManager

    // Initialize ViewModel at the Activity level so the splash screen can access it
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep native splash screen visible until start destination is resolved
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.startDestination.value is StartDestination.Loading
        }

        enableEdgeToEdge()

        // Start real-time sync for Firebase
        syncManager.startRealTimeSync()

        setContent {
            val isDarkMode by mainViewModel.isDarkMode.collectAsStateWithLifecycle()
            val languageCode by mainViewModel.languageCode.collectAsStateWithLifecycle()

            // Automatically apply locale changes when DataStore updates
            LaunchedEffect(languageCode) {
                val appLocale = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            SafwaTheme(darkTheme = isDarkMode) {
                SafwaNavHost(mainViewModel = mainViewModel)
            }
        }
    }
}