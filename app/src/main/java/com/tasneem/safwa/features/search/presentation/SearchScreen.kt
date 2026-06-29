package com.tasneem.safwa.features.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()

) {
    val state by viewModel.state.collectAsState()
    
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SafwaTopAppBar(
                title = stringResource(id = R.string.search),
                onBackClick = { /* Handle back navigation */ },
                onCartClick = { /* Handle cart navigation */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchInput(
                query = state.searchQuery,
                onQueryChanged = { onIntent(SearchIntent.QueryChanged(it)) },
                onExecuteSearch = { onIntent(SearchIntent.ExecuteSearch) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            CategoriesRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { onIntent(SearchIntent.FilterSelected(it)) }
            )

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
                    items(state.filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = false,
                            onToggleFavorite = { onIntent(SearchIntent.ToggleFavorite(product)) },
                            onClick = { onIntent(SearchIntent.ProductClicked(product)) }
                        )
                    }
                }
            }
        }
    }
}
