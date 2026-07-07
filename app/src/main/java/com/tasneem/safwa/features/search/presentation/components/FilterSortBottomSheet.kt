package com.tasneem.safwa.features.search.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.tasneem.safwa.R
import com.tasneem.safwa.features.search.presentation.SearchIntent
import com.tasneem.safwa.features.search.presentation.SearchState
import com.tasneem.safwa.features.search.presentation.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortBottomSheet(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.sort_and_filter),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = stringResource(id = R.string.sort_by),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            SortOptionRow(
                text = stringResource(id = R.string.sort_none),
                selected = state.selectedSortOption == SortOption.NONE,
                onClick = { onIntent(SearchIntent.UpdateSortOption(SortOption.NONE)) }
            )
            SortOptionRow(
                text = stringResource(id = R.string.sort_price_low_high),
                selected = state.selectedSortOption == SortOption.PRICE_LOW_TO_HIGH,
                onClick = { onIntent(SearchIntent.UpdateSortOption(SortOption.PRICE_LOW_TO_HIGH)) }
            )
            SortOptionRow(
                text = stringResource(id = R.string.sort_price_high_low),
                selected = state.selectedSortOption == SortOption.PRICE_HIGH_TO_LOW,
                onClick = { onIntent(SearchIntent.UpdateSortOption(SortOption.PRICE_HIGH_TO_LOW)) }
            )
            SortOptionRow(
                text = stringResource(id = R.string.sort_best_seller),
                selected = state.selectedSortOption == SortOption.BEST_SELLER,
                onClick = { onIntent(SearchIntent.UpdateSortOption(SortOption.BEST_SELLER)) }
            )

            Divider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

            Text(
                text = stringResource(id = R.string.price_range),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(
                    id = R.string.price_format,
                    state.selectedPriceRange.start.toInt(),
                    state.selectedPriceRange.endInclusive.toInt(),
                    state.currentCurrency
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RangeSlider(
                value = state.selectedPriceRange,
                onValueChange = { onIntent(SearchIntent.UpdatePriceRange(it)) },
                valueRange = 0f..state.maxPrice,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

            if (state.availableBrands.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.brands),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                state.availableBrands.forEach { brand ->
                    FilterCheckboxRow(
                        text = brand,
                        checked = state.selectedBrands.contains(brand),
                        onCheckedChange = { onIntent(SearchIntent.ToggleBrandFilter(brand)) }
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            }

            // Filtering Section - Sub Categories (Tags)
            if (state.availableSubCategories.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.sub_categories),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                state.availableSubCategories.forEach { subCategory ->
                    FilterCheckboxRow(
                        text = subCategory,
                        checked = state.selectedSubCategories.contains(subCategory),
                        onCheckedChange = { onIntent(SearchIntent.ToggleSubCategoryFilter(subCategory)) }
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            }

            // Grouping Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.group_by_sub_category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.isGroupedBySubCategory,
                    onCheckedChange = { onIntent(SearchIntent.ToggleGroupBySubCategory(it)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { onIntent(SearchIntent.ClearFilters) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(id = R.string.clear_all), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onIntent(SearchIntent.ApplyFilters) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(id = R.string.apply), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SortOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FilterCheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
