package com.tasneem.safwa.features.settings.savedaddresses.presentation.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.settings.savedaddresses.domain.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdownField(
    countries: List<Country>,
    selected: Country?,
    onSelect: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(selected) { mutableStateOf(selected?.name ?: "") }

    val filtered = remember(query, countries) {
        if (query.isBlank()) countries
        else countries.filter { it.name.contains(query, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            label = { Text(stringResource(R.string.country)) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { country ->
                DropdownMenuItem(
                    text = { Text(country.name) },
                    onClick = {
                        onSelect(country)
                        query = country.name
                        expanded = false
                    }
                )
            }
        }
    }
}