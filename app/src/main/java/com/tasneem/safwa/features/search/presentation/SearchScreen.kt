package com.tasneem.safwa.features.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.search.presentation.components.EmptySearchState
import com.tasneem.safwa.features.search.presentation.components.SearchInput
import com.tasneem.safwa.features.wishlist.presentation.components.CategoriesRow
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard
import com.tasneem.safwa.features.search.presentation.components.FilterSortBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToProductDetails: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToProductDetails ->
                    onNavigateToProductDetails(effect.handle)
            }
        }
    }

    SearchContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        SafwaTopAppBar(
            title = stringResource(id = R.string.search),
        )
            SearchInput(
                query = state.searchQuery,
                onQueryChanged = { onIntent(SearchIntent.QueryChanged(it)) },
                onExecuteSearch = { onIntent(SearchIntent.ExecuteSearch) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(end = 8.dp)
            ) {
                CategoriesRow(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { onIntent(SearchIntent.FilterSelected(it)) },
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { onIntent(SearchIntent.ToggleFilterSheet(true)) }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter and Sort"
                    )
                }
            }
            
            if (state.showFilterSheet) {
                FilterSortBottomSheet(
                    state = state,
                    onIntent = onIntent,
                    onDismissRequest = { onIntent(SearchIntent.ToggleFilterSheet(false)) }
                )
            }

            if (state.filteredProducts.isEmpty()) {
                EmptySearchState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.isGroupedBySubCategory) {
                        val grouped = state.filteredProducts.groupBy { it.tags.firstOrNull() ?: "Other" }
                        grouped.forEach { (subCategory, products) ->
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                androidx.compose.material3.Text(
                                    text = subCategory,
                                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(products, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    isFavorite = state.favoriteProductIds.contains(product.id),
                                    onToggleFavorite = { onIntent(SearchIntent.ToggleFavorite(product)) },
                                    onClick = { onIntent(SearchIntent.ProductClicked(product)) }
                                )
                            }
                        }
                    } else {
                        items(state.filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = state.favoriteProductIds.contains(product.id),
                                onToggleFavorite = { onIntent(SearchIntent.ToggleFavorite(product)) },
                                onClick = { onIntent(SearchIntent.ProductClicked(product)) }
                            )
                        }
                    }
                }
            }
        }
    }
