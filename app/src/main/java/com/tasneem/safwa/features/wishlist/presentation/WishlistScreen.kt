package com.tasneem.safwa.features.wishlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.wishlist.presentation.components.CategoriesRow
import com.tasneem.safwa.features.wishlist.presentation.components.EmptyWishlistState
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WishlistScreen(
    modifier: Modifier = Modifier,
    onNavigateToProductDetails: (String) -> Unit = {},
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WishlistEffect.NavigateToProductDetails ->
                    onNavigateToProductDetails(effect.handle)
            }
        }
    }

    WishlistContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistContent(
    state: WishlistState,
    onIntent: (WishlistIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        SafwaTopAppBar(
            title = stringResource(id = R.string.wishlist),
        )
            CategoriesRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { onIntent(WishlistIntent.FilterSelected(it)) }
            )

            if (state.filteredProducts.isEmpty()) {
                EmptyWishlistState()
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
                            isFavorite = true,
                            onToggleFavorite = { onIntent(WishlistIntent.ToggleFavorite(product)) },
                            onClick = { onIntent(WishlistIntent.ProductClicked(product)) }
                        )
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    val sampleProducts = listOf(
        Product(
            "1",
            "Hydrogen Snowboard",
            "hydrogen",
            "Desc 1",
            "Snowboard Co",
            "Snowboard",
            "199.99",
            "USD",
            listOf(""),
            listOf("")
        ),
        Product(
            "2",
            "Helium Snowboard",
            "helium",
            "Desc 2",
            "Snowboard Co",
            "Snowboard",
            "249.99",
            "USD",
            listOf(""),
            listOf("")
        ),
        Product(
            "3",
            "Lithium Snowboard",
            "lithium",
            "Desc 3",
            "Snowboard Co",
            "Snowboard",
            "299.99",
            "USD",
            listOf(""),
            listOf("")
        )
    )
    WishlistContent(
        state = WishlistState(products = sampleProducts),
        onIntent = {}
    )
}
