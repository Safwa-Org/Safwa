package com.tasneem.safwa.features.wishlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.wishlist.domain.model.Product
import com.tasneem.safwa.features.wishlist.presentation.components.CategoriesRow
import com.tasneem.safwa.features.wishlist.presentation.components.EmptyWishlistState
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    state: WishlistState,
    onEvent: (WishlistEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.wishlist), 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CategoriesRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onEvent = onEvent
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
                            onToggleFavorite = { onEvent(WishlistEvent.ToggleFavorite(product)) },
                            onClick = { onEvent(WishlistEvent.ProductClicked(product)) }
                        )
                    }
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
            ""
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
            ""
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
            ""
        )
    )
    WishlistScreen(
        state = WishlistState(products = sampleProducts),
        onEvent = {}
    )
}
