package com.tasneem.safwa.features.latest_products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.core.presentation.component.ErrorContentWithRetry
import com.tasneem.safwa.features.latest_products.viewmodel.LatestProductsViewModel
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard

@Composable
fun LatestProductsScreen(
    viewModel: LatestProductsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit
) {
    val products = viewModel.productsPagingFlow.collectAsLazyPagingItems()
    val favoriteProductIds by viewModel.favoriteProductIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 18.dp)) {
                SafwaTopAppBar(
                    title = stringResource(id = R.string.latest_added), 
                    onBackClick = onNavigateBack
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (products.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (products.loadState.refresh is LoadState.Error) {
                val e = products.loadState.refresh as LoadState.Error
                ErrorContentWithRetry(
                    title = stringResource(R.string.an_unexpected_error_occurred),
                    description = e.error.localizedMessage ?: stringResource(R.string.error_generic_desc),
                    onRetry = { products.retry() }
                )
            } else if (products.itemCount == 0) {
                Text(text = stringResource(id = R.string.no_products_found), modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products.itemCount) { index ->
                        val product = products[index]
                        if (product != null) {
                            ProductCard(
                                product = product,
                                isFavorite = favoriteProductIds.contains(product.id),
                                onToggleFavorite = { viewModel.toggleFavorite(product) },
                                onClick = { onNavigateToProductDetails(product.handle) }
                            )
                        }
                    }

                    if (products.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    } else if (products.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Button(onClick = { products.retry() }, modifier = Modifier.align(Alignment.Center)) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
