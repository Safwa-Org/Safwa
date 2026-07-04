package com.tasneem.safwa.features.settings.savedaddresses.presentation.view

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEffect
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesEvent
import com.tasneem.safwa.features.settings.savedaddresses.presentation.state.SavedAddressesState
import com.tasneem.safwa.features.settings.savedaddresses.presentation.viewmodel.SavedAddressesViewModel

@Composable
fun SavedAddressesScreen(
    viewModel: SavedAddressesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SavedAddressesEffect.NavigateBack -> onNavigateBack()
                is SavedAddressesEffect.ShowError -> {
                    snackbarHostState.showSnackbar(message = context.getString(effect.messageResId))
                }
            }
        }
    }

    SavedAddressesContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )

    if (uiState.showEditDialog) {
        AddressEditDialog(
            initialAddress = uiState.addressToEdit,
            nameErrorResId = uiState.recipientNameErrorResId,
            mobileErrorResId = uiState.mobileNumberErrorResId,
            addressErrorResId = uiState.addressErrorResId,
            countries = uiState.countries,
            selectedCountry = uiState.selectedCountry,
            addressQuery = uiState.addressQuery,
            suggestions = uiState.suggestions,
            isSearchingAddress = uiState.isSearchingAddress,
            onCountrySelected = { viewModel.onEvent(SavedAddressesEvent.CountrySelected(it)) },
            onAddressQueryChange = { viewModel.onEvent(SavedAddressesEvent.AddressQueryChanged(it)) },
            onSuggestionSelected = { viewModel.onEvent(SavedAddressesEvent.SuggestionSelected(it)) },
            onDismiss = { viewModel.onEvent(SavedAddressesEvent.DismissDialogs) },
            onSave = { label, recipientName, mobileNumber ->
                viewModel.onEvent(SavedAddressesEvent.SaveAddress(label, recipientName, mobileNumber))
            }
        )
    }
    if (uiState.addressToDelete != null) {
        ConfirmDeleteDialog(
            onDismiss = { viewModel.onEvent(SavedAddressesEvent.DismissDialogs) },
            onConfirm = { viewModel.onEvent(SavedAddressesEvent.ConfirmDeleteAddress) }
        )
    }
}

@Composable
fun SavedAddressesContent(
    state: SavedAddressesState,
    snackbarHostState: SnackbarHostState,
    onEvent: (SavedAddressesEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize().padding(top = 24.dp, bottom = 0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SafwaTopAppBar(
                title = stringResource(R.string.savedaddresses),
                onBackClick = { onEvent(SavedAddressesEvent.BackClicked) },
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
                    onClick = { onEvent(SavedAddressesEvent.AddNewAddressClicked) },
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
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Add new address",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.addresses, key = { it.id }) { address ->
                AddressCard(
                    address = address,
                    onEditClick = { onEvent(SavedAddressesEvent.EditAddressClicked(address)) },
                    onDeleteClick = { onEvent(SavedAddressesEvent.DeleteAddressClicked(address)) }
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SavedAddressesScreenPreview() {
    SafwaTheme {
        SavedAddressesContent(
            state = SavedAddressesState(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {}
        )
    }
}