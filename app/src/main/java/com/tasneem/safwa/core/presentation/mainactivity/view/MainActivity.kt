package com.tasneem.safwa.core.presentation.mainactivity.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.data.sync.SessionSyncManager
import com.tasneem.safwa.core.navigation.SafwaNavHost
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.core.theme.SafwaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // EXTEND AppCompatActivity

    @Inject
    lateinit var syncManager: SessionSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        syncManager.startRealTimeSync()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val isDarkMode by mainViewModel.isDarkMode.collectAsStateWithLifecycle()
            val languageCode by mainViewModel.languageCode.collectAsStateWithLifecycle()

            // Automatically apply locale changes when DataStore updates
            LaunchedEffect(languageCode) {
                val appLocale = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            SafwaTheme(darkTheme = isDarkMode) {
                SafwaNavHost()
            }
        }
    }
}