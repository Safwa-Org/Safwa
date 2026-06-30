package com.tasneem.safwa.features.profile.presentation.view.languageandcurrency

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.profile.presentation.view.components.SectionTitle
import com.tasneem.safwa.features.profile.presentation.view.components.SettingsDivider
import com.tasneem.safwa.features.profile.presentation.view.components.SettingsGroupCard

data class AppLanguage(val code: String, val title: String, val subtitle: String)
data class AppCurrency(val code: String, val name: String, val flag: String)

@Composable
fun LanguageAndCurrencyScreen(
    // viewModel: ProfileViewModel = hiltViewModel(),
    // onNavigateBack: () -> Unit = {}
) {
    // State will eventually come from ViewModel
    var selectedLanguageCode by remember { mutableStateOf("en") }
    var selectedCurrencyCode by remember { mutableStateOf("SAR") }

    val languages = listOf(
        AppLanguage("en", "English", "United Kingdom"),
        AppLanguage("ar", "العربية", "مصر")
    )

    val dummyCurrencies = listOf(
        AppCurrency("EGP", "Egyptian Pound", "🇪🇬") ,
        AppCurrency("SAR", "Saudi Riyal", "🇸🇦"),
        AppCurrency("AED", "Emirati Dirham", "🇦🇪"),
        AppCurrency("QAR", "Qatari Riyal", "🇶🇦"),
        AppCurrency("USD", "US Dollar", "🇺🇸")
    )

    LanguageAndCurrencyContent(
        selectedLanguageCode = selectedLanguageCode,
        selectedCurrencyCode = selectedCurrencyCode,
        languages = languages,
        currencies = dummyCurrencies,
        onLanguageSelected = { selectedLanguageCode = it },
        onCurrencySelected = { selectedCurrencyCode = it },
        onSavePreferences = { /* TODO */ }
    )
}

@Composable
fun LanguageAndCurrencyContent(
    selectedLanguageCode: String,
    selectedCurrencyCode: String,
    languages: List<AppLanguage>,
    currencies: List<AppCurrency>,
    onLanguageSelected: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onSavePreferences: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Using Scaffold to easily stick the "Save preferences" button to the bottom
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SafwaTopAppBar(
                title = stringResource(R.string.langandcurrency),
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.shadow(elevation = 8.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = onSavePreferences,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        // Placeholder string resource
                        text = stringResource(R.string.save_preferences),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // --- APP LANGUAGE SECTION ---
            SectionTitle(title = stringResource(R.string.app_language)) // Placeholder
            Spacer(modifier = Modifier.height(16.dp))
            SettingsGroupCard {
                languages.forEachIndexed { index, language ->
                    RadioSettingsItem(
                        title = language.title,
                        subtitle = language.subtitle,
                        selected = language.code == selectedLanguageCode,
                        onClick = { onLanguageSelected(language.code) },
                        leadingIcon = { LanguageIcon(code = language.code) }
                    )
                    if (index < languages.lastIndex) {
                        SettingsDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CURRENCY SECTION ---
            SectionTitle(title = stringResource(R.string.currency)) // Placeholder
            Spacer(modifier = Modifier.height(16.dp))
            SettingsGroupCard {
                currencies.forEachIndexed { index, currency ->
                    RadioSettingsItem(
                        title = currency.name,
                        subtitle = "${currency.code} · ${currency.name}",
                        selected = currency.code == selectedCurrencyCode,
                        onClick = { onCurrencySelected(currency.code) },
                        leadingIcon = { FlagIcon(flagEmoji = currency.flag) }
                    )
                    if (index < currencies.lastIndex) {
                        SettingsDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun LanguageAndCurrencyScreenPreview() {
    SafwaTheme {
        LanguageAndCurrencyScreen()
    }
}