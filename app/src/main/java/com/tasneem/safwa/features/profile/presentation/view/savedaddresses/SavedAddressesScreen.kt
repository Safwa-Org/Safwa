package com.tasneem.safwa.features.profile.presentation.view.savedaddresses
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

// Dummy data class
data class SavedAddress(
    val id: String,
    val label: String,
    val isDefault: Boolean,
    val recipientName: String,
    val street: String,
    val cityAndZip: String,
    val mobileNumber: String
)

@Composable
fun SavedAddressesScreen(
    // viewModel: ProfileViewModel = hiltViewModel(),
    // onNavigateBack: () -> Unit = {}
) {
    // Dummy Data - Will eventually come from ViewModel
    var addresses by remember {
        mutableStateOf(
            listOf(
                SavedAddress(
                    id = "1",
                    label = "Home",
                    isDefault = true,
                    recipientName = "Aisha Al-Marri",
                    street = "Al Olaya District, King Fahd Rd.",
                    cityAndZip = "Riyadh 12241, KSA",
                    mobileNumber = "+966 55 204 8891"
                ),
                SavedAddress(
                    id = "2",
                    label = "Office",
                    isDefault = false,
                    recipientName = "Aisha Al-Marri",
                    street = "Olaya Tower 2, Floor 14",
                    cityAndZip = "Riyadh 12333, KSA",
                    mobileNumber = "+966 55 204 8891"
                )
            )
        )
    }

    // State for the Edit Dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<SavedAddress?>(null) }

    // State for the Delete Dialog
    var addressToDelete by remember { mutableStateOf<SavedAddress?>(null) }

    SavedAddressesContent(
        addresses = addresses,
        onAddNewClick = {
            addressToEdit = null
            showEditDialog = true
        },
        onEditClick = { address ->
            addressToEdit = address
            showEditDialog = true
        },
        onDeleteClick = { address ->
            addressToDelete = address
        }
    )

    if (showEditDialog) {
        AddressEditDialog(
            initialAddress = addressToEdit,
            onDismiss = { showEditDialog = false },
            onSave = { updatedAddress ->
                addresses = if (addressToEdit == null) {
                    addresses + updatedAddress.copy(id = System.currentTimeMillis().toString())
                } else {
                    addresses.map { if (it.id == updatedAddress.id) updatedAddress else it }
                }
                showEditDialog = false
            }
        )
    }

    if (addressToDelete != null) {
        ConfirmDeleteDialog(
            onDismiss = { addressToDelete = null },
            onConfirm = {
                addresses = addresses.filter { it.id != addressToDelete?.id }
                addressToDelete = null
            }
        )
    }
}

@Composable
fun SavedAddressesContent(
    addresses: List<SavedAddress>,
    onAddNewClick: () -> Unit,
    onEditClick: (SavedAddress) -> Unit,
    onDeleteClick: (SavedAddress) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SafwaTopAppBar(
                title = stringResource(R.string.savedaddresses)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.shadow(elevation = 8.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = onAddNewClick,
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(addresses, key = { it.id }) { address ->
                AddressCard(
                    address = address,
                    onEditClick = { onEditClick(address) },
                    onDeleteClick = { onDeleteClick(address) }
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
        SavedAddressesScreen()
    }
}