package com.tasneem.safwa.core.presentation.mainactivity.view

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.compose.setContent
import java.util.Locale
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.data.sync.SessionSyncManager
import com.tasneem.safwa.core.navigation.SafwaNavHost
import com.tasneem.safwa.core.navigation.StartDestination
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.core.theme.SafwaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // Keep AppCompatActivity for language switching

    @Inject
    lateinit var syncManager: SessionSyncManager

    // Initialize ViewModel at the Activity level so the splash screen can access it
    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("LocalContextConfigurationRead")
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

            val layoutDirection = if (languageCode == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            val context = LocalContext.current
            val localeContext = remember(languageCode, context) {
                val locale = Locale(languageCode)
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                val configContext = context.createConfigurationContext(config)
                
                object : ContextWrapper(context) {
                    override fun getResources(): Resources {
                        return configContext.resources
                    }
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalContext provides localeContext
            ) {
                SafwaTheme(darkTheme = isDarkMode) {
                    SafwaNavHost(mainViewModel = mainViewModel)
                }
            }
        }
    }
}