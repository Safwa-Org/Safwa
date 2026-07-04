package com.tasneem.safwa.features.brand.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.brand.presentation.state.BrandProductsEffect
import com.tasneem.safwa.features.brand.presentation.state.BrandProductsIntent
import com.tasneem.safwa.features.brand.presentation.viewmodel.BrandProductsViewModel
import com.tasneem.safwa.features.core.presentation.component.ErrorContentWithRetry
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BrandProductsScreen(
    viewModel: BrandProductsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is BrandProductsEffect.NavigateBack -> onNavigateBack()
                is BrandProductsEffect.NavigateToProductDetails ->
                    onNavigateToProductDetails(effect.handle)
            }
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 18.dp)) {
                SafwaTopAppBar(
                    title = viewModel.brandName,
                    onBackClick = { viewModel.handleIntent(BrandProductsIntent.OnBackClick) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorContentWithRetry(
                        title = stringResource(uiState.error!!.titleRes),
                        description = stringResource(uiState.error!!.descriptionRes),
                        onRetry = { viewModel.handleIntent(BrandProductsIntent.LoadProducts) },
                    )
                }

                uiState.products.isEmpty() -> {
                    Text(
                        text = stringResource(id = R.string.no_brand_products_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.products, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = uiState.favoriteProductIds.contains(product.id),
                                onToggleFavorite = {
                                    viewModel.handleIntent(BrandProductsIntent.ToggleFavorite(product))
                                },
                                onClick = {
                                    viewModel.handleIntent(BrandProductsIntent.OnProductClick(product))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
