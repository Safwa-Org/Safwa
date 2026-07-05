package com.tasneem.safwa.features.settings.savedaddresses.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.AddressCandidate

@Composable
fun AddressAutocompleteField(
    query: String,
    suggestions: List<AddressCandidate>,
    isSearching: Boolean,
    enabled: Boolean,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (AddressCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(if (enabled) stringResource(R.string.search_for_your_address) else stringResource(
                R.string.select_a_country_first
            )) },
            enabled = enabled,
            singleLine = true,
            trailingIcon = { if (isSearching) CircularProgressIndicator(modifier = Modifier.size(18.dp)) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (suggestions.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Column {
                    suggestions.forEach { candidate ->
                        Text(
                            text = candidate.displayLabel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionSelected(candidate) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}