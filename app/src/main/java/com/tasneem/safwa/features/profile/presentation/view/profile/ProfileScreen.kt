package com.tasneem.safwa.features.profile.presentation.view.profile

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.profile.presentation.view.components.SectionTitle
import com.tasneem.safwa.features.profile.presentation.view.components.SettingsDivider
import com.tasneem.safwa.features.profile.presentation.view.components.SettingsGroupCard
import com.tasneem.safwa.features.profile.presentation.view.components.SettingsItem

@Composable
fun ProfileScreen(
    // viewModel: ProfileViewModel = hiltViewModel(),
    // onNavigateToOrderHistory: () -> Unit = {}
) {
    //     val uiState by viewModel.state.collectAsStateWithLifecycle()

    /*    LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    ProgileEffect.NavigateTo -> {}
                    ProgileEffect.NavigateTo -> {}
                    ProgileEffect.NavigateTo -> {}
                    ProgileEffect.NavigateTo -> {}
                }
            }
        }*/
    ProfileContent(
        //state = uiState,
        //onEvent = viewModel::onEvent
    )
}

@Composable
fun ProfileContent(
    //state: HomeState,
    //onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Temporary state to hold switch value until you link it with your ViewModel
    var isDarkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        SafwaTopAppBar(
            stringResource(R.string.profile),
        )
        /*************************************************************************************/
        // this is now static will be saved in shared prefs to data source to repo to usecase to vm to state
        // Profile Header
        ProfileHeader(
            firstName = "Osama",
            lastName = "Khaled",
            email = "osama@safwa.com",
            photoUrl = null,
            isElite = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        ProfileStatsRow(
            ordersCount = "12",
            wishlistCount = "8",
            points = "2.4k"
        )

        Spacer(modifier = Modifier.height(32.dp))
        /***************************************************************************/

        SectionTitle(title = stringResource(R.string.accountcapital))
        Spacer(modifier = Modifier.height(16.dp))
        SettingsGroupCard {
            SettingsItem(title = stringResource(R.string.orderhistory),
                /*this should be added dynamically*/
                subtitle = "12"+ stringResource(R.string.singlespace) + stringResource(R.string.orders))
            SettingsDivider()
            SettingsItem(title = stringResource(R.string.savedaddresses),
                /*this should be added dynamically*/
                subtitle = "2"+ stringResource(R.string.singlespace) + stringResource(R.string.addresses))
            SettingsDivider()
            SettingsItem(title = stringResource(R.string.langandcurrency),
                /*this should be added dynamically*/
                subtitle = "English · USD", showDivider = false)
        }

        Spacer(modifier = Modifier.height(32.dp))
        SectionTitle(title = stringResource(R.string.appearance))
        Spacer(modifier = Modifier.height(16.dp))
        SettingsGroupCard {
            SettingsSwitchItem(
                title = stringResource(R.string.darkmode),
                subtitle = "",
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { /* TODO */ },
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
        ProfileScreen()
    }
}