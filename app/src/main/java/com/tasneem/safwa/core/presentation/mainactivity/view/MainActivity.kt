package com.tasneem.safwa.core.presentation.mainactivity.view

import android.content.Intent
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
import com.tasneem.safwa.core.data.sync.SessionSyncManager
import com.tasneem.safwa.core.navigation.SafwaNavHost
import com.tasneem.safwa.core.navigation.StartDestination
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.payment.presentation.state.PaymentEvent
import com.tasneem.safwa.features.payment.presentation.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // Keep AppCompatActivity for language switching

    @Inject
    lateinit var syncManager: SessionSyncManager

    // Initialize ViewModel at the Activity level so the splash screen can access it
    private val mainViewModel: MainViewModel by viewModels()

    private val paymentViewModel: PaymentViewModel by viewModels()

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

        handlePayPalIntent(intent)

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePayPalIntent(intent)
    }

    private fun handlePayPalIntent(intent: Intent) {
        val data = intent.data ?: return
        if (data.scheme != "safwa" || data.host != "paypal") return

        val success = data.path?.contains("return") == true
        paymentViewModel.onEvent(PaymentEvent.PayPalPaymentCompleted(success = success))
    }
}