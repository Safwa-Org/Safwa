package com.tasneem.safwa.features.settings.profile.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.LoadingOverlay
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.settings.core.presentation.view.components.SectionTitle
import com.tasneem.safwa.features.settings.core.presentation.view.components.SettingsDivider
import com.tasneem.safwa.features.settings.core.presentation.view.components.SettingsGroupCard
import com.tasneem.safwa.features.settings.core.presentation.view.components.SettingsItem
import com.tasneem.safwa.features.settings.profile.presentation.view.component.ProfileHeader
import com.tasneem.safwa.features.settings.profile.presentation.view.component.SettingsSwitchItem
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEffect
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileEvent
import com.tasneem.safwa.features.settings.profile.presentation.state.ProfileState
import com.tasneem.safwa.features.settings.profile.presentation.view.component.LogoutConfirmDialog
import com.tasneem.safwa.features.settings.profile.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToSavedAddresses: () -> Unit = {},
    onNavigateToLanguageAndCurrency: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToCreateAccount: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToOrderHistory -> onNavigateToOrderHistory()
                is ProfileEffect.NavigateToSavedAddresses -> onNavigateToSavedAddresses()
                is ProfileEffect.NavigateToLanguageAndCurrency -> onNavigateToLanguageAndCurrency()
                is ProfileEffect.NavigateToLogin -> onNavigateToSignIn()
                is ProfileEffect.NavigateToCreateAccount -> onNavigateToCreateAccount()
                is ProfileEffect.ShowError -> {
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileContent(
            state = uiState,
            onEvent = viewModel::onEvent
        )
        LoadingOverlay(visible = uiState.isLoggingOut)
    }

    if (uiState.showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            onConfirm = { viewModel.onEvent(ProfileEvent.ConfirmLogout) },
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissLogoutDialog) }
        )
    }
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isGuest) {
        GuestProfileContent(state = state, onEvent = onEvent, modifier = modifier)
        return
    }

    val scrollState = rememberScrollState()

    val formattedPoints = if (state.points >= 1000) {
        "${state.points / 1000.0}k".replace(".0k", "k")
    } else {
        state.points.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        SafwaTopAppBar(
            title = stringResource(R.string.profile),
        )

        ProfileHeader(
            firstName = state.firstName,
            lastName = state.lastName,
            email = state.email,
            photoUrl = state.photoUrl,
            isElite = state.isElite
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(title = stringResource(R.string.accountcapital))
        Spacer(modifier = Modifier.height(16.dp))
        SettingsGroupCard {
            SettingsItem(
                title = stringResource(R.string.orderhistory),
                subtitle = "${state.ordersCount}" + stringResource(R.string.singlespace) + stringResource(R.string.orders),
                modifier = Modifier.clickable { onEvent(ProfileEvent.OrderHistoryClicked) }
            )
            SettingsDivider()
            SettingsItem(
                title = stringResource(R.string.savedaddresses),
                subtitle = "${state.savedAddressesCount}" + stringResource(R.string.singlespace) + stringResource(R.string.addresses),
                modifier = Modifier.clickable { onEvent(ProfileEvent.SavedAddressesClicked) }
            )
            SettingsDivider()
            SettingsItem(
                title = stringResource(R.string.langandcurrency),
                subtitle = "${state.language} · ${state.currency}",
                showDivider = false,
                modifier = Modifier.clickable { onEvent(ProfileEvent.LanguageAndCurrencyClicked) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        SectionTitle(title = stringResource(R.string.appearance))
        Spacer(modifier = Modifier.height(16.dp))
        SettingsGroupCard {
            SettingsSwitchItem(
                title = stringResource(R.string.darkmode),
                subtitle = "",
                checked = state.isDarkMode,
                onCheckedChange = { isChecked ->
                    onEvent(ProfileEvent.DarkModeToggled(isChecked))
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { onEvent(ProfileEvent.LogoutClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun GuestProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        SafwaTopAppBar(
            title = stringResource(R.string.profile),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(64.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.guest_profile_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.guest_profile_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onEvent(ProfileEvent.SignInClicked) },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onEvent(ProfileEvent.CreateAccountClicked) },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SectionTitle(title = stringResource(R.string.appearance))
        Spacer(modifier = Modifier.height(16.dp))
        SettingsGroupCard {
            SettingsSwitchItem(
                title = stringResource(R.string.darkmode),
                subtitle = "",
                checked = state.isDarkMode,
                onCheckedChange = { isChecked ->
                    onEvent(ProfileEvent.DarkModeToggled(isChecked))
                }
            )
            SettingsDivider()
            SettingsItem(
                title = stringResource(R.string.langandcurrency),
                subtitle = "${state.language} · ${state.currency}",
                showDivider = false,
                modifier = Modifier.clickable { onEvent(ProfileEvent.LanguageAndCurrencyClicked) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ProfileScreenPreview() {
    SafwaTheme {
        // Supply a dummy state and empty event handler for the preview
        ProfileContent(
            state = ProfileState(
                firstName = "Osama",
                lastName = "Khaled",
                email = "osama@safwa.com",
                isElite = true,
                ordersCount = 12,
                wishlistCount = 8,
                savedAddressesCount = 2,
                points = 2400,
                language = "English",
                currency = "USD",
                isDarkMode = false
            ),
            onEvent = {}
        )
    }
}