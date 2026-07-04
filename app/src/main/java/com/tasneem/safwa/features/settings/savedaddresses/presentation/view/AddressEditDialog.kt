package com.tasneem.safwa.features.settings.savedaddresses.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaLogo
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Address
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country

@Composable
fun AddressEditDialog(
    initialAddress: Address?,
    nameErrorResId: Int?,
    mobileErrorResId: Int?,
    addressErrorResId: Int?,
    countries: List<Country>,
    selectedCountry: Country?,
    addressQuery: String,
    suggestions: List<AddressCandidate>,
    isSearchingAddress: Boolean,
    onCountrySelected: (Country) -> Unit,
    onAddressQueryChange: (String) -> Unit,
    onSuggestionSelected: (AddressCandidate) -> Unit,
    onDismiss: () -> Unit,
    onSave: (label: String, recipientName: String, mobileNumber: String) -> Unit
) {
    var label by remember { mutableStateOf(initialAddress?.label ?: "") }
    var recipientName by remember { mutableStateOf(initialAddress?.recipientName ?: "") }
    var mobileNumber by remember { mutableStateOf(initialAddress?.mobileNumber ?: "") }

    var isNameDirty by remember { mutableStateOf(false) }
    var isMobileDirty by remember { mutableStateOf(false) }

    LaunchedEffect(initialAddress) {
        isNameDirty = false
        isMobileDirty = false
    }

    val textFieldShape = RoundedCornerShape(16.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    SafwaLogo(size = 56.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (initialAddress == null)
                        stringResource(R.string.address_add_title)
                    else
                        stringResource(R.string.address_edit_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.address_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = textFieldShape
                )

                Spacer(modifier = Modifier.height(12.dp))

                val hasNameError = nameErrorResId != null && !isNameDirty
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it; isNameDirty = true },
                    label = { Text(stringResource(R.string.address_recipient_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasNameError,
                    supportingText = if (hasNameError && nameErrorResId != null) {
                        { Text(stringResource(nameErrorResId)) }
                    } else null,
                    shape = textFieldShape
                )

                Spacer(modifier = Modifier.height(12.dp))

                val hasMobileError = mobileErrorResId != null && !isMobileDirty
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it; isMobileDirty = true },
                    label = { Text(stringResource(R.string.address_mobile_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasMobileError,
                    supportingText = if (hasMobileError && mobileErrorResId != null) {
                        { Text(stringResource(mobileErrorResId)) }
                    } else null,
                    shape = textFieldShape
                )

                Spacer(modifier = Modifier.height(12.dp))

                CountryDropdownField(
                    countries = countries,
                    selected = selectedCountry,
                    onSelect = onCountrySelected,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                AddressAutocompleteField(
                    query = addressQuery,
                    suggestions = suggestions,
                    isSearching = isSearchingAddress,
                    enabled = selectedCountry != null,
                    onQueryChange = onAddressQueryChange,
                    onSuggestionSelected = onSuggestionSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                if (addressErrorResId != null) {
                    Text(
                        text = stringResource(addressErrorResId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.secondary)
                    }

                    Button(
                        onClick = {
                            isNameDirty = false
                            isMobileDirty = false
                            onSave(label.ifBlank { "Address" }, recipientName, mobileNumber)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}