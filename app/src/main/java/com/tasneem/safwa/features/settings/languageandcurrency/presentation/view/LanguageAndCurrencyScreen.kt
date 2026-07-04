package com.tasneem.safwa.features.settings.languageandcurrency.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.settings.core.presentation.view.components.SectionTitle
import com.tasneem.safwa.features.settings.core.presentation.view.components.SettingsDivider
import com.tasneem.safwa.features.settings.core.presentation.view.components.SettingsGroupCard
import com.tasneem.safwa.features.settings.languageandcurrency.domain.model.Currency
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyEffect
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyEvent
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.state.LanguageAndCurrencyState
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.viewmodel.LanguageAndCurrencyViewModel

data class AppLanguage(val code: String, val title: String, val subtitle: String)

@Composable
fun LanguageAndCurrencyScreen(
    viewModel: LanguageAndCurrencyViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LanguageAndCurrencyEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    val languages = listOf(
        AppLanguage("en", "English", "United Kingdom"),
        AppLanguage("ar", "العربية", "مصر")
    )

    LanguageAndCurrencyContent(
        state = uiState,
        languages = languages,
        currencies = uiState.availableCurrencies,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LanguageAndCurrencyContent(
    state: LanguageAndCurrencyState,
    languages: List<AppLanguage>,
    currencies: List<Currency>,
    onEvent: (LanguageAndCurrencyEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 0.dp),
        topBar = {
            SafwaTopAppBar(
                title = stringResource(R.string.langandcurrency),
                onBackClick = { onEvent(LanguageAndCurrencyEvent.BackClicked) }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .shadow(elevation = 8.dp)
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()),
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = { onEvent(LanguageAndCurrencyEvent.SavePreferencesClicked) },
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
            SectionTitle(title = stringResource(R.string.app_language))
            Spacer(modifier = Modifier.height(16.dp))
            SettingsGroupCard {
                languages.forEachIndexed { index, language ->
                    RadioSettingsItem(
                        title = language.title,
                        subtitle = language.subtitle,
                        selected = language.code == state.selectedLanguageCode,
                        onClick = { onEvent(LanguageAndCurrencyEvent.LanguageSelected(language.code)) },
                        leadingIcon = { LanguageIcon(code = language.code) }
                    )
                    if (index < languages.lastIndex) {
                        SettingsDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CURRENCY SECTION ---
            SectionTitle(title = stringResource(R.string.currency))
            Spacer(modifier = Modifier.height(16.dp))

            if (currencies.isEmpty() && state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(top = 16.dp)
                )
            } else {
                SettingsGroupCard {
                    currencies.forEachIndexed { index, currency ->
                        RadioSettingsItem(
                            title = currency.name,
                            subtitle = "${currency.code} · ${currency.name}",
                            selected = currency.code == state.selectedCurrencyCode,
                            onClick = { onEvent(LanguageAndCurrencyEvent.CurrencySelected(currency.code)) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    Text(
                                        text = currency.flagEmoji,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }                        )
                        if (index < currencies.lastIndex) {
                            SettingsDivider()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}